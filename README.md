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

PS：测试时需要修改秒杀活动时间seckill_goods表开始和结束时间，然后确保库存足够。  
   

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


中奖名单：
```html
中奖名单
【排名：1】，用户名：user4，秒杀商品： iPhone X
【排名：2】，用户名：user2，秒杀商品： iPhone X
【排名：3】，用户名：user69，秒杀商品： iPhone X
【排名：4】，用户名：user6，秒杀商品： iPhone X
【排名：5】，用户名：user7，秒杀商品： iPhone X
【排名：6】，用户名：user9，秒杀商品： iPhone X
【排名：7】，用户名：user0，秒杀商品： iPhone X
【排名：8】，用户名：user1，秒杀商品： iPhone X
【排名：9】，用户名：user56，秒杀商品： iPhone X
【排名：10】，用户名：user59，秒杀商品： iPhone X
【排名：11】，用户名：user119，秒杀商品： iPhone X
【排名：12】，用户名：user122，秒杀商品： iPhone X
【排名：13】，用户名：user123，秒杀商品： iPhone X
【排名：14】，用户名：user100，秒杀商品： iPhone X
【排名：15】，用户名：user999，秒杀商品： iPhone X
【排名：16】，用户名：user127，秒杀商品： iPhone X
【排名：17】，用户名：user137，秒杀商品： iPhone X
【排名：18】，用户名：user160，秒杀商品： iPhone X
【排名：19】，用户名：user5，秒杀商品： iPhone X
【排名：20】，用户名：user146，秒杀商品： iPhone X
【排名：21】，用户名：user157，秒杀商品： iPhone X
【排名：22】，用户名：user180，秒杀商品： iPhone X
【排名：23】，用户名：user185，秒杀商品： iPhone X
【排名：24】，用户名：user435，秒杀商品： iPhone X
【排名：25】，用户名：user60，秒杀商品： iPhone X
【排名：26】，用户名：user125，秒杀商品： iPhone X
【排名：27】，用户名：user139，秒杀商品： iPhone X
【排名：28】，用户名：user183，秒杀商品： iPhone X
【排名：29】，用户名：user403，秒杀商品： iPhone X
【排名：30】，用户名：user433，秒杀商品： iPhone X
【排名：31】，用户名：user8，秒杀商品： iPhone X
【排名：32】，用户名：user130，秒杀商品： iPhone X
【排名：33】，用户名：user138，秒杀商品： iPhone X
【排名：34】，用户名：user151，秒杀商品： iPhone X
【排名：35】，用户名：user158，秒杀商品： iPhone X
【排名：36】，用户名：user414，秒杀商品： iPhone X
【排名：37】，用户名：user421，秒杀商品： iPhone X
【排名：38】，用户名：user156，秒杀商品： iPhone X
【排名：39】，用户名：user385，秒杀商品： iPhone X
【排名：40】，用户名：user428，秒杀商品： iPhone X
【排名：41】，用户名：user109，秒杀商品： iPhone X
【排名：42】，用户名：user111，秒杀商品： iPhone X
【排名：43】，用户名：user143，秒杀商品： iPhone X
【排名：44】，用户名：user153，秒杀商品： iPhone X
【排名：45】，用户名：user438，秒杀商品： iPhone X
【排名：46】，用户名：user175，秒杀商品： iPhone X
【排名：47】，用户名：user379，秒杀商品： iPhone X
【排名：48】，用户名：user436，秒杀商品： iPhone X
【排名：49】，用户名：user188，秒杀商品： iPhone X
【排名：50】，用户名：user241，秒杀商品： iPhone X
【排名：51】，用户名：user422，秒杀商品： iPhone X
【排名：52】，用户名：user442，秒杀商品： iPhone X
【排名：53】，用户名：user129，秒杀商品： iPhone X
【排名：54】，用户名：user140，秒杀商品： iPhone X
【排名：55】，用户名：user162，秒杀商品： iPhone X
【排名：56】，用户名：user174，秒杀商品： iPhone X
【排名：57】，用户名：user430，秒杀商品： iPhone X
【排名：58】，用户名：user64，秒杀商品： iPhone X
【排名：59】，用户名：user85，秒杀商品： iPhone X
【排名：60】，用户名：user104，秒杀商品： iPhone X
【排名：61】，用户名：user113，秒杀商品： iPhone X
【排名：62】，用户名：user135，秒杀商品： iPhone X
【排名：63】，用户名：user142，秒杀商品： iPhone X
【排名：64】，用户名：user166，秒杀商品： iPhone X
【排名：65】，用户名：user168，秒杀商品： iPhone X
【排名：66】，用户名：user171，秒杀商品： iPhone X
【排名：67】，用户名：user417，秒杀商品： iPhone X
【排名：68】，用户名：user61，秒杀商品： iPhone X
【排名：69】，用户名：user154，秒杀商品： iPhone X
【排名：70】，用户名：user167，秒杀商品： iPhone X
【排名：71】，用户名：user173，秒杀商品： iPhone X
【排名：72】，用户名：user3，秒杀商品： iPhone X
【排名：73】，用户名：user62，秒杀商品： iPhone X
【排名：74】，用户名：user63，秒杀商品： iPhone X
【排名：75】，用户名：user105，秒杀商品： iPhone X
【排名：76】，用户名：user107，秒杀商品： iPhone X
【排名：77】，用户名：user110，秒杀商品： iPhone X
【排名：78】，用户名：user116，秒杀商品： iPhone X
【排名：79】，用户名：user126，秒杀商品： iPhone X
【排名：80】，用户名：user131，秒杀商品： iPhone X
【排名：81】，用户名：user132，秒杀商品： iPhone X
【排名：82】，用户名：user134，秒杀商品： iPhone X
【排名：83】，用户名：user141，秒杀商品： iPhone X
【排名：84】，用户名：user155，秒杀商品： iPhone X
【排名：85】，用户名：user159，秒杀商品： iPhone X
【排名：86】，用户名：user165，秒杀商品： iPhone X
【排名：87】，用户名：user169，秒杀商品： iPhone X
【排名：88】，用户名：user172，秒杀商品： iPhone X
【排名：89】，用户名：user181，秒杀商品： iPhone X
【排名：90】，用户名：user186，秒杀商品： iPhone X
【排名：91】，用户名：user444，秒杀商品： iPhone X
【排名：92】，用户名：user101，秒杀商品： iPhone X
【排名：93】，用户名：user108，秒杀商品： iPhone X
【排名：94】，用户名：user112，秒杀商品： iPhone X
【排名：95】，用户名：user150，秒杀商品： iPhone X
【排名：96】，用户名：user177，秒杀商品： iPhone X
【排名：97】，用户名：user425，秒杀商品： iPhone X
【排名：98】，用户名：user439，秒杀商品： iPhone X
【排名：99】，用户名：user136，秒杀商品： iPhone X
【排名：100】，用户名：user416，秒杀商品： iPhone X
```

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
