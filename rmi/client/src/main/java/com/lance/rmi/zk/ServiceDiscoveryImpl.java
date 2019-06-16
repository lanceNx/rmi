package com.lance.rmi.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public class ServiceDiscoveryImpl implements IServiceDiscovery {

    private CuratorFramework curatorFramework;

    public List<String> instances = new ArrayList<String>();

    private String address;

    public ServiceDiscoveryImpl(String address) {
        this.address = address;
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNNECTION_STR).
                retryPolicy(new ExponentialBackoffRetry(1000, 10)).
                sessionTimeoutMs(5000).build();

        curatorFramework.start();
    }

    public String discover(String serviceName) throws Exception {
        String path = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;
        System.out.println(path);
        try {
            //获取该节点下所有子节点
            instances = curatorFramework.getChildren().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取子节点异常 -> " + e);
        }

        //监听节点信息变化
        registerWatcher(path);

        //负载均衡机制
        LoadBanalce loadBanalce = new RandomLoadBanalce();
        System.out.println();
        return loadBanalce.selectHost(instances);
    }

    private void registerWatcher(final String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, false);
        //如果子节点有变更，更新节点信息
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                instances = curatorFramework.getChildren().forPath(path);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

}
