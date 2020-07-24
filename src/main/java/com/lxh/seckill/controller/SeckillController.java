package com.lxh.seckill.controller;

import com.lxh.seckill.annotations.AccessLimit;
import com.lxh.seckill.bo.GoodsBo;
import com.lxh.seckill.common.Const;
import com.lxh.seckill.entity.OrderInfo;
import com.lxh.seckill.entity.SeckillOrder;
import com.lxh.seckill.entity.User;
import com.lxh.seckill.mq.MQSender;
import com.lxh.seckill.mq.SeckillMessage;
import com.lxh.seckill.redis.GoodsKey;
import com.lxh.seckill.redis.RedisService;
import com.lxh.seckill.redis.UserKey;
import com.lxh.seckill.result.CodeMsg;
import com.lxh.seckill.result.Result;
import com.lxh.seckill.service.SeckillGoodsService;
import com.lxh.seckill.service.SeckillOrderService;
import com.lxh.seckill.service.UserService;
import com.lxh.seckill.util.CookieUtil;
import com.lxh.seckill.websocket.WebSocketServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.*;

@Controller
@RequestMapping("seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    RedisService redisService;
    @Autowired
    SeckillGoodsService seckillGoodsService;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    MQSender mqSender;
    @Autowired
    UserService userService;
    @Autowired
    WebSocketServer webSocketServer;
    // 库存
    private volatile long stock = 0;
    // 本地缓存
    private ConcurrentHashMap<Long/*商品Id*/, Boolean/*库存是否还有*/> localOverMap = new ConcurrentHashMap<Long, Boolean>();


    /**
     * 系统初始化
     */
    public void afterPropertiesSet() throws Exception {
        List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
        if (goodsList == null) {
            return;
        }
        for (GoodsBo goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), goods.getStockCount(), Const.RedisCacheExtime.GOODS_LIST);
            localOverMap.put(goods.getId(), false);
        }
    }


    /**
     * 秒杀访问地址
     * @param model
     * @param goodsId
     * @param path
     * @param request
     * @return
     */
    @AccessLimit(seconds=5, maxCount=2, needLogin=true)
    @RequestMapping(value = "/{path}/seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model,
                                @RequestParam("goodsId") long goodsId,
                                @RequestParam("type") Integer type,
                                @PathVariable("path") String path,
                                HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        // 验证path
        boolean check = seckillOrderService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        // 模拟1000人高并发
        if (type == 1){
            long startTime = System.currentTimeMillis();
            System.out.println("秒杀开始时间："+startTime);
            List<User> users = userService.selectAll();
            int size = users.size();
            ExecutorService executorService = Executors.newCachedThreadPool();
            CyclicBarrier barrier = new CyclicBarrier(size);
            for (int i = 0; i < size; i++) {
                int finalI = i;
                int finalI1 = i;
                executorService.execute(()->{
                    try {
                        barrier.await();
                        // 1000个人模拟高并发
                        businessDoHandler(users.get(finalI), goodsId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            System.out.println("秒杀结束时间："+ (System.currentTimeMillis() - startTime));
        }
        // 模拟一个人
        else {
            long startTime = System.currentTimeMillis();
            System.out.println("秒杀开始时间："+startTime);
            businessDoHandler(user, goodsId);
            System.out.println("秒杀结束时间："+ (System.currentTimeMillis() - startTime));
        }
        // 排队中
        return Result.success(0);
    }

    private void businessDoHandler(User user, long goodId){
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodId);
        if (over){
            System.out.println(String.format("【内存标记】用户：%s，秒杀失败：%s", user.getUserName(), CodeMsg.MIAO_SHA_OVER.getMsg()));
            return;
        }
        //预减库存（volatile保证原子可见性）
        stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodId);
        if (stock < 0) {
            System.out.println(String.format("【预减库存】用户：%s，秒杀失败：%s", user.getUserName(), CodeMsg.MIAO_SHA_OVER.getMsg()));
            localOverMap.put(goodId, true);
            return;
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodId);
        if (order != null) {
            System.out.println(String.format("用户：%s，秒杀失败：%s", user.getUserName(), CodeMsg.REPEATE_MIAOSHA.getMsg()));
            return;
        }
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodId);
        mm.setTime(System.currentTimeMillis());
        mqSender.sendSeckillMessage(mm);
    }

    /**
     * 客户端轮询查询是否下单成功
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(@RequestParam("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        long result = seckillOrderService.getSeckillResult((long) user.getId(), goodsId);
        return Result.success(result);
    }


    /**
     * 获取秒杀地址
     * @param request
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, User user,
                                         @RequestParam("goodsId") long goodsId) {
        String loginToken = CookieUtil.readLoginToken(request);
        user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        String path = seckillOrderService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    /**
     * 预热库存
     * @param goodsId
     * @param request
     * @return
     */
    @RequestMapping(value = "/hot", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> hotResult(@RequestParam("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        GoodsBo goodsBo = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        if (goodsBo == null) {
            return Result.error(CodeMsg.NO_GOODS);
        }
        redisService.set(GoodsKey.getSeckillGoodsStock, "" + goodsBo.getId(), goodsBo.getStockCount(), Const.RedisCacheExtime.GOODS_LIST);
        localOverMap.put(goodsBo.getId(), false);
        return Result.success("预热成功");
    }
}
