package com.lxh.seckill.other.lock;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * created by lanxinghua@2dfire.com on 2020/7/25
 * zk实现分布式锁
 */
@Component
public class Lock5 implements InitializingBean {
    private static final String CONNECTION="zk1.2dfire-daily.com:2181,zk1.2dfire-daily.com:2181,zk1.2dfire-daily.com:2181";
    private static final String ROOT_PATH_LOCK = "rootLock";
    private static CuratorFramework client;
    static {
        // 重试策略，初始化每次重试之间需要等待的时间，基准等待时间为1秒。
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        // 使用默认的会话时间（60秒）和连接超时时间（15秒）来创建 Zookeeper 客户端
        client = CuratorFrameworkFactory.builder().
                connectString(CONNECTION).
                connectionTimeoutMs(15 * 1000).
                sessionTimeoutMs(60 * 100).
                retryPolicy(retryPolicy).
                build();
        // 启动客户端
        client.start();
    }

    /**
     * 获取分布式锁
     */
    public Boolean tryLock(String path) {
        String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
        try {
            client.create()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.EPHEMERAL)
            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
            .forPath(keyPath);
            System.out.println("success to acquire lock for path " + keyPath);
            return true;
        } catch (Exception e) {
            System.out.println("failed to acquire lock for path");
            return false;
        }
    }


    /**
     * 释放分布式锁
     */
    public boolean unLock(String path) {
        try {
            String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
            if (client.checkExists().forPath(keyPath) != null) {
                client.delete().forPath(keyPath);
            }
        } catch (Exception e) {
            System.out.println("failed to release lock");
            return false;
        }
        return true;
    }


    //创建父节点，并创建永久节点
    @Override
    public void afterPropertiesSet() throws Exception {
        client = client.usingNamespace("lock-namespace");
        String path = "/" + ROOT_PATH_LOCK;
        try {
            if (client.checkExists().forPath(path) == null) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path);
            }
            System.out.println("root path 的 watcher 事件创建成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @SneakyThrows
    public static void main(String[] args) {
       //zk();
        curator();
    }


    /**
     * java连接zk
     */
    @SneakyThrows
    public static void zk(){
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper(CONNECTION, 3000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
                System.out.println("Watch =>" + event.getType());
            }
        });
        countDownLatch.await();
        System.out.println(zk.getState());
        String node = "/lxh";
        Stat state = zk.exists(node, false);

        if (state == null) {
            System.out.println("创建节点");
            String createResult = zk.create(node, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println(createResult);
        }

        byte[] b = zk.getData(node, false, state);
        System.out.println("获取data值 =》" + new String(b));

        state = zk.exists(node, false);
        state = zk.setData(node, "1".getBytes(), state.getVersion());
        System.out.println("after update, version changed to =>" + state.getVersion());

        zk.delete(node,state.getVersion());
        System.out.println("delete complete");
        zk.close();
    }


    /**
     * curator连接zk
     */
    @SneakyThrows
    public static void curator() {
        // 重试策略，初始化每次重试之间需要等待的时间，基准等待时间为1秒。
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        // 使用默认的会话时间（60秒）和连接超时时间（15秒）来创建 Zookeeper 客户端
        @Cleanup
        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString(CONNECTION).
                connectionTimeoutMs(15 * 1000).
                sessionTimeoutMs(60 * 100).
                retryPolicy(retryPolicy).
                build();
        // 启动客户端
        client.start();


        Stat stat = client.checkExists().forPath("/lxh");
        Object o;
        if (stat == null) {
            o = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/lxh", "0".getBytes());
        }

        //更新时使用State
        stat = client.setData().withVersion(stat.getVersion()).forPath("/lxh", "1".getBytes());
        System.out.println("update => " + stat.getVersion());
    }

}
