## 高并发秒杀系统：SpringBoot+Mybatis+Redis+RocketMq 

和Mater更新内容说明
>1.秒杀地址隐藏分析    
>2.demo解决超卖实现方案       
>3.demo实现分布式锁3种方案    
>4.整合sentinel进行限流   

---
### 一、秒杀地址隐藏分析
1.关键字段：活动商品id，用户id获取到md5加密数据
```html
String str = MD5Util.md5(UUID.randomUUID()+"123456");
redisService.set(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, str , Const.RedisCacheExtime.GOODS_ID);
```
2.校验秒杀地址  
```html
public boolean checkPath(User user, long goodsId, String path) {
    if(user == null || path == null) {
        return false;
    }
    String pathOld = redisService.get(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, String.class);
    return path.equals(pathOld);
}
```
---
     
### 二、解决超卖实现的3种方式  
com.lxh.seckill.OverSlodTest

##### 2.1 Mysql排他锁  
```sql
update goods set num = num - 1 WHERE id = 1001 and num > 0
```

排他锁又称为写锁，简称X锁，顾名思义，排他锁就是不能与其他所并存，如一个事务获取了一个数据行的排
他锁，其他事务就不能再获取该行的其他锁，包括共享锁和排他锁，但是获取排他锁的事务是可以对数据就
行读取和修改。就是类似于我在执行update操作的时候，这一行是一个事务(默认加了排他锁)。这一行不能
被任何其他线程修改和读写。


##### 2.2 乐观锁版本控制
```sql
select version from goods WHERE id= 1001
update goods set num = num - 1, version = version + 1 WHERE id= 1001 AND num > 0 AND version = @version(上面查到的version);
```   
这种方式采用了版本号的方式，其实也就是CAS的原理。

 
##### 2.3 Redis单线程预减库存 
利用redis的单线程预减库存。比如商品有100件。那么我在redis存储一个k,v。例如 <gs1001, 100>   
每一个用户线程进来，key值就减1，等减到0的时候，全部拒绝剩下的请求。   
那么也就是只有100个线程会进入到后续操作。所以一定不会出现超卖的现象。

总结：第二种CAS是失败重试，并无加锁。应该比第一种加锁效率要高很多。类似于Java中的Synchronize和CAS。

---
### 三、实现分布式锁3种方案
###### 3.1 Mysql 

###### 3.2 Redis

###### 3.3 Zk


