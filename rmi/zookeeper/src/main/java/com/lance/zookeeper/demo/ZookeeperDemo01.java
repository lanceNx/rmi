package com.lance.zookeeper.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.hamcrest.core.Every;

import java.util.concurrent.CountDownLatch;

public class ZookeeperDemo01 {
    public static String HOST_PORT = "47.99.223.159:2181";

    /**
     * None (-1),
     * 客户端链接状态发生变化的时候，会
     * 收到 none 的事件
     * NodeCreated (1),
     * 创建节点的事件。 比如 zk-lance
     * NodeDeleted (2),
     * 删除节点的事件
     * NodeDataChanged (3), 节点数据发生变更
     * NodeChildrenChanged (4); 子节点被创建、被删除、会
     */
    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //连接zk server的ip和端口
        //会话超时时长
        //监听
        ZooKeeper zk = new ZooKeeper(HOST_PORT, 4000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState().equals(Event.KeeperState.SyncConnected)) {
                    System.out.println("Defalut Watcher conn:" + watchedEvent.getType());
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        //Thread.sleep(2000);
        ZooKeeper.States state = zk.getState();
        System.out.println(state);

        //创建节点路径  节点数据  节点权限 什么类型的节点 比如 临时节点
        zk.create("/lance", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        Stat stat = new Stat();
        zk.getData("/lance", true, stat);

        //节点路径 节点数据 节点版本
        zk.setData("/lance", "1".getBytes(), stat.getVersion());

        Thread.sleep(5000);
        zk.getData("/lance", true, stat);
        zk.delete("/lance",stat.getVersion());
        System.in.read();
        zk.close();
    }

}
