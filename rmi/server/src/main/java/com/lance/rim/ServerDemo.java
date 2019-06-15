package com.lance.rim;

import com.lance.rim.zk.IRegisterCenter;
import com.lance.rim.zk.RegisterCenterImpl;

public class ServerDemo {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        RpcServer rpcServer = new RpcServer(registerCenter,"127.0.0.1:8080");
        rpcServer.bind(userService);
        rpcServer.publisher();
    }

}
