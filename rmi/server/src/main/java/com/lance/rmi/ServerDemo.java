package com.lance.rmi;

import com.lance.rmi.zk.IRegisterCenter;
import com.lance.rmi.zk.RegisterCenterImpl;

/**
 * @author Lance
 * 手写JAVA原生RPC框架 RMI
 * 1.0版本
 */
public class ServerDemo {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        RpcServer rpcServer = new RpcServer(registerCenter,"127.0.0.1:8080");
        rpcServer.bind(userService);
        rpcServer.publisher();
    }

}
