package com.lance.rim;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteInvocationHandler implements InvocationHandler {

    private String host;
    private Integer port;

    public RemoteInvocationHandler(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestObject requestObject = new RequestObject();
        requestObject.setClassName(method.getDeclaringClass().getName());
        requestObject.setMethodName(method.getName());
        requestObject.setArgs(args);
        System.out.println(requestObject);
        TCPTransport tcpTransport = new TCPTransport(this.host, this.port);

        return tcpTransport.send(requestObject);
    }
}
