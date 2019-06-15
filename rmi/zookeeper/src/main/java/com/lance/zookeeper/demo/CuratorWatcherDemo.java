package com.lance.zookeeper.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorWatcherDemo {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZookeeperDemo01.HOST_PORT).
                //重试策略
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).
                sessionTimeoutMs(5000).
                namespace("curator").build();
        //启动
        curatorFramework.start();
//        addListernerWithNodeCache(curatorFramework,"/lance");
//        addListernerWithPathChildCache(curatorFramework,"/lance");
        addListernerWithTreeCache(curatorFramework,"/lance");
        System.in.read();
    }


    /**
     * PathChildCache 监听一个节点下子节点的创建、删除、更新
     * NodeCache 监听一个节点的更新和创建
     * TreeCache 综合PathcChildCache和NodeCache的特性
     */
    public static void addListernerWithTreeCache(CuratorFramework curatorFramework, String path) throws Exception {
        TreeCache treeCache = new TreeCache(curatorFramework,path);
        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println(path + "节点的更新 创建 以及 子节点的 增删改操作啦");
            }
        };
        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }


    /**
     * PathChildCache 监听一个节点下子节点的创建、删除、更新
     */
    public static void addListernerWithPathChildCache(CuratorFramework curatorFramework, String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework,path,false);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("节点下子节点的 增 删 改 的回调");
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }


    /**
     * NodeCache 监听一个节点的更新和创建
     */
    public static void addListernerWithNodeCache(CuratorFramework curatorFramework, String path) throws Exception {
        NodeCache nodeCache = new NodeCache(curatorFramework, path, false);

        //创建一个nodeCache监听器
        NodeCacheListener nodeCacheListener = new NodeCacheListener() {
            @Override//监听一个节点 发生创建和修改操作 进行回调
            public void nodeChanged() throws Exception {
                System.out.println("NodeCache ： 监听一个节点的更新和创建"+nodeCache.getCurrentData().getStat());
            }
        };
        //将
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }
}
