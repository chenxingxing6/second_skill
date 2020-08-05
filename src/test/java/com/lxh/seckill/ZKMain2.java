package com.lxh.seckill;

import com.alibaba.fastjson.JSON;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * created by lanxinghua@2dfire.com on 2020/7/30
 * zk通知客户端测试
 */
public class ZKMain2 {
    // 会话超时时间
    private static final int SESSION_TIMEOUT = 30 * 1000;
    // zk实例
    private static ZooKeeper zk;
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

    private static void createZkInstance() throws Exception
    {
        zk = new ZooKeeper("10.1.21.202:2181", ZKMain2.SESSION_TIMEOUT, ZKMain2.watcher);
    }

    private static void zkClose() throws Exception{
        zk.close();
    }


    public static void main(String[] args) throws Exception{
        createZkInstance();
        List<String> children = zk.getChildren("/lxh", true);
        for (String child : children) {
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);
        zkClose();
    }
}
