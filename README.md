### 高并发秒杀系统：SpringBoot+Mybatis+Redis+RocketMq 

感谢这位博主（基于上面进行优化）：https://github.com/hfbin/Seckill   

![秒杀](https://upload-images.jianshu.io/upload_images/13864900-625d2ae682866ae5.jpg)

---
![系统架构图](https://upload-images.jianshu.io/upload_images/13864900-366352040a8b7d43.png)

---
### 项目启动说明
> 1、启动前，进行相关redis、mysql、rocketMq地址   
2、登录地址：http://localhost:8888/page/login      
3、商品秒杀列表地址：http://localhost:8888/goods/list   
4、账号：18077200000，密码：123456   
   

---
### 模拟高并发
1、数据库共有一千个用户左右（手机号：从18077200000~18077200998 密码为：123456）    
2、使用CyclicBarrier模拟高并发，1000个用户秒杀某个商品  
3、读：Redis
4、写：RocketMq

```html
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
```

---
### 技术实现
1、页面缓存、商品详情静态化、订单静态化 
2、消息队列RocketMq进行流量肖峰     
4、接口限流防刷   
5、解决超卖问题   

---
### 页面截图
登录页
![登陆](https://upload-images.jianshu.io/upload_images/13864900-5bdb1820affb779c.png)

商品列表情页
![列表](https://upload-images.jianshu.io/upload_images/13864900-cdb86de6e2ef9dcd.png)

商品秒杀页面
![123](https://upload-images.jianshu.io/upload_images/13864900-dde49598d04e637d.png)

日志：
```html
秒杀开始时间：1595554505880
1410080408加入webSocket！当前人数为1
秒杀结束时间：123
2020-07-24 09:35:06.018  INFO 31432 --- [pool-1-thread-5] com.lxh.seckill.mq.MQSender              : send message:{"goodsId":1,"time":1595554506013,"user":{"head":"","id":1410080412,"lastLoginDate":1531880980000,"loginCount":1,"password":"ae2fe40a6242ef07a35a30da2232e10a","phone":"18077200004","registerDate":1531880980000,"salt":"9d5b364d","userName":"user4"}}
2020-07-24 09:35:06.157  INFO 31432 --- [pool-1-thread-3] com.lxh.seckill.mq.MQSender              : send message:{"goodsId":1,"time":1595554506157,"user":{"head":"","id":1410080410,"lastLoginDate":1531880980000,"loginCount":1,"password":"ae2fe40a6242ef07a35a30da2232e10a","phone":"18077200002","registerDate":1531880980000,"salt":"9d5b364d","userName":"user2"}}
2020-07-24 09:35:06.205  INFO 31432 --- [ool-1-thread-70] com.lxh.seckill.mq.MQSender              : send message:{"goodsId":1,"time":1595554506205,"user":{"head":"","id":1410080477,"lastLoginDate":1531880980000,"loginCount":1,"password":"ae2fe40a6242ef07a35a30da2232e10a","phone":"18077200069","registerDate":1531880980000,"salt":"9d5b364d","userName":"user69"}}
【预减库存】用户：user490，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user212，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user420，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user78，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user449，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user409，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user405，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user395，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user445，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user402，秒杀失败：商品已经秒杀完毕
【预减库存】用户：user507，秒杀失败：商品已经秒杀完毕
....
```

订单详情页
![订单详情页](https://upload-images.jianshu.io/upload_images/13864900-703c5e0ad3f04ec4.png)

---
