package com.lance.rmi;

import com.lance.rmi.zk.IServiceDiscovery;
import com.lance.rmi.zk.ServiceDiscoveryImpl;
import com.lance.rmi.zk.ZkConfig;

/**
 * @author Lance
 * 手写JAVA原生RPC框架 RMI
 * 1.0版本
 */
public class CilentDemo {

    public static void main(String[] args) {
        //先创建一个连接
        IServiceDiscovery serviceDiscovery = new
                ServiceDiscoveryImpl(ZkConfig.CONNNECTION_STR);
        //拿到一个代理对象
        RpcClientProxy rpcClientPorxy = new RpcClientProxy(serviceDiscovery);
        UserService userService = rpcClientPorxy.clientProxy(
                UserService.class, serviceDiscovery);

        System.out.println(userService.loveYou("Lance", "WWH"));
    }


}
