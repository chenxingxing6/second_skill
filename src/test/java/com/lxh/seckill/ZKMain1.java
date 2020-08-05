package com.lxh.seckill;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * created by lanxinghua@2dfire.com on 2020/7/30
 * zk API测试
 */
public class ZKMain1 {
    // 会话超时时间
    private static final int SESSION_TIMEOUT = 30 * 1000;
    // zk实例
    private static ZooKeeper zk;
    // 创建watch实例
    private static Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("watchEvent----->" + watchedEvent.toString());
        }
    };

    private static void createZkInstance() throws Exception
    {
        zk = new ZooKeeper("10.1.21.202:2181", ZKMain1.SESSION_TIMEOUT, ZKMain1.watcher);
    }

    private static void zkClose() throws Exception{
        zk.close();
    }

    private static void opt() throws Exception{
        System.out.println("------------start-------------");
        // 创建节点
        // zk.create("/mylxh1", "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        // 获取节点数据
        byte[] data = zk.getData("/mylxh1", ZKMain1.watcher, null);
        System.out.println(new String(data));

        // 修改节点数据
        Stat stat = zk.setData("/mylxh1", "updateData1111".getBytes(), -1);
        data = zk.getData("/mylxh1", ZKMain1.watcher, null);
        System.out.println(new String(data) + "；" + stat.getVersion());

        // 乐观锁修改数据
        stat = zk.setData("/mylxh1", "updateDataVersion".getBytes(), stat.getVersion());
        data = zk.getData("/mylxh1", ZKMain1.watcher, null);
        System.out.println(new String(data) + "；" + stat.getVersion());

        // 删除节点
        //zk.delete("/mylxh1", -1);

        // 节点是否存在
        Stat exists = zk.exists("/mylxh1", false);
        System.out.println(exists.toString());
        System.out.println("-------------end--------------");
    }

    public static void main(String[] args) throws Exception{
        createZkInstance();
        opt();
        zkClose();
    }
}
