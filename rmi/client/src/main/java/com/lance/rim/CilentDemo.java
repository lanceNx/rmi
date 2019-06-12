package com.lance.rim;

/**
 * 手写JAVA原生RPC框架 RMI
 * 1.0版本
 */
public class CilentDemo {

    public static void main(String[] args) {

        RpcClientProxy rpcClientPorxy = new RpcClientProxy();
        UserService userService = rpcClientPorxy.clientProxy(
                UserService.class,"localhost",8888);

        System.out.println(userService.loveYou("Lance","wwh"));
    }


}
