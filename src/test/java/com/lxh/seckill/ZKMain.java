package com.lxh.seckill;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * created by lanxinghua@2dfire.com on 2020/7/30
 * zk发现通知
 */
public class ZKMain implements Watcher {
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat  stat = new Stat();
    private static final String RootPath = "/mylxh";


    public static void main(String[] args) throws Exception{
        // 连接zk
        zk = new ZooKeeper("10.1.21.202:2181", 5000, new ZKMain());
        countDownLatch.await();
        String str = new String(zk.getData(RootPath, true, stat));
        System.out.println("原始数据：" + str);
        TimeUnit.SECONDS.sleep(1);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
            System.out.println("zk连接成功通知");

        }
        System.out.println("*****");
    }
}
