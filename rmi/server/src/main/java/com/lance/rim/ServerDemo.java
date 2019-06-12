package com.lance.rim;

public class ServerDemo {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.publisher(userService, 8888);
    }

}
