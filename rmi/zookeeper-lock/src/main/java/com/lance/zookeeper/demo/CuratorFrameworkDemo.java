package com.lance.zookeeper.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class CuratorFrameworkDemo {

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZookeeperDemo01.HOST_PORT).
                //重试策略
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).
                sessionTimeoutMs(5000).
                namespace("curator").build();

        //启动
        curatorFramework.start();
        //创建节点
        curatorFramework.create().
                //如果未创建父节点，则创建
                creatingParentsIfNeeded().
                //创建什么类型的节点
                withMode(CreateMode.PERSISTENT).
                forPath("/lance/node2", "01".getBytes());

        Stat stat = new Stat();
        curatorFramework.getData().
                storingStatIn(stat).
                forPath("/lance/node1");

        curatorFramework.setData().
                withVersion(stat.getVersion()).
                forPath("/lance/node1","100".getBytes());
        //System.in.read();
        curatorFramework.close();
    }
}
