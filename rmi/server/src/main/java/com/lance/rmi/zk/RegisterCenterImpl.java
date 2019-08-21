package com.lance.rmi.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class RegisterCenterImpl implements IRegisterCenter {

    private CuratorFramework curatorFramework;

    {
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNNECTION_STR).
                retryPolicy(new ExponentialBackoffRetry(1000, 10)).
                sessionTimeoutMs(5000).build();

        curatorFramework.start();
    }

    public void register(String serviceName, String serviceAddress) {
        String servicePath = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;
        try {
            if (null == curatorFramework.checkExists().forPath(servicePath)) {
                curatorFramework.create().
                        creatingParentsIfNeeded().
                        //创建持久型节点
                        withMode(CreateMode.PERSISTENT).
                        forPath(servicePath, "0".getBytes());
            }
            // /registrys/UserService/192.168.0.100
            //                       /192.168.0.101
            String addressPath = servicePath + "/" + serviceAddress;
            //创建临时节点
            curatorFramework.create().
                    creatingParentsIfNeeded().
                    //创建临时节点
                    withMode(CreateMode.EPHEMERAL).
                    forPath(addressPath, "0".getBytes());

            System.out.println("注册成功 -> SUCCESS ");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("注册失败 -> ERROR " + e);
        }
    }
}
