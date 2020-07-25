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

###### 3.1 基于数据库实现
```sql
CREATE TABLE `my_lock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gmt_modify` datetime DEFAULT NULL,
  `lock_desc` varchar(255) DEFAULT '',
  `lock_type` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_key` (`lock_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

1）唯一索引 UNIQUE KEY
当想要锁住某个方法时执行insert方法，插入一条数据，lock_type有唯一约束，可以保证多次提交只有一次成功，而成功的
这次就可以认为其获得了锁，而执行完成后执行delete语句释放锁。

缺点：
> 1.强依赖与数据库   
> 2.非阻塞的，获取失败直接失败  
> 3.没有失效时间  
> 4.非重入锁 


---

2）乐观锁
```html
-- 线程1查询，当前left_count为1，则有记录，当前版本号为1234
select left_count, version from t_bonus where id = 10001 and left_count > 0

-- 线程2查询，当前left_count为1，有记录，当前版本号为1234
select left_count, version from t_bonus where id = 10001 and left_count > 0

-- 线程1,更新完成后当前的version为1235，update状态为1，更新成功
update t_bonus set version = 1235, left_count = left_count-1 where id = 10001 and version = 1234

-- 线程2,更新由于当前的version为1235，udpate状态为0，更新失败，再针对相关业务做异常处理
update t_bonus set version = 1235, left_count = left_count-1 where id = 10001 and version = 1234
```

3）悲观锁（排他锁）for update
在查询语句后面增加for update，数据库会在查询过程中给数据库表增加排他锁。当某条记录被加上排他锁之后，其他线程无法再在该行记录上增加排他锁。
我们可以认为获得排它锁的线程即可获得分布式锁，当获取到锁之后，可以执行方法的业务逻辑，执行完方法之后，再通过connection.commit();操作来释放锁


---
###### 3.2 Redis
1.setnx(lockkey, 1) 如果返回0，则说明占位失败；如果返回1，则说明占位成功   
2.expire()命令对lockkey设置超时时间，为的是避免死锁问题。    
3.执行完业务代码后，可以通过delete命令删除key。     
```html
 try {
    Long isLock = redisService.setnx(key, 10, String.valueOf(user.getId()));
    if (isLock == 1){
        System.out.println("do business....");
        TimeUnit.SECONDS.sleep(10);
    }
    System.out.println("没获取到锁.....");
}catch (Exception e){
    e.printStackTrace();
}
finally {
    String s = redisService.get(key, String.class);
    if (s.equals(user.getId())){
        redisService.del(key);
    }
}
```

###### 3.3 Zk


