## ZK学习笔记

---
###### 1.监听客户端
```html
发生改变：["a"]
15:52:37.207 [main-SendThread(10.1.21.202:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Got WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/lxh for sessionid 0x900000039bd805e
watchEvent----->path:/lxh
发生改变：["a","b"]
15:53:23.737 [main-SendThread(10.1.21.202:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Got WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/lxh for sessionid 0x900000039bd805e
watchEvent----->path:/lxh
发生改变：["a","b","c"]
15:54:07.525 [main-SendThread(10.1.21.202:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Got WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/lxh for sessionid 0x900000039bd805e
watchEvent----->path:/lxh
15:54:07.528 [main-SendThread(10.1.21.202:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x900000039bd805e, packet:: clientPath:null serverPath:null finished:false header:: 5,8  replyHeader:: 5,5575399976837,0  request:: '/lxh,T  response:: v{'a,'b} 
发生改变：["a","b"]
```

```html
// 创建watch实例
private static Watcher watcher = new Watcher() {
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("watchEvent----->path:" + watchedEvent.getPath());
        try {
            // 设置true表示监听此事件，zk就会在监听器列表注册该事件
            List<String> children = zk.getChildren("/lxh", true);
            System.out.println("发生改变："+JSON.toJSONString(children));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
};
```

---

