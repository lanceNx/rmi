package com.lance.rmi;

import com.lance.rmi.zk.IServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteInvocationHandler implements InvocationHandler {

    private IServiceDiscovery serviceDiscovery;
    private String version;

    public RemoteInvocationHandler(IServiceDiscovery serviceDiscovery, String version) {
        this.serviceDiscovery = serviceDiscovery;
        this.version = version;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestObject requestObject = new RequestObject();
        requestObject.setClassName(method.getDeclaringClass().getName());
        requestObject.setMethodName(method.getName());
        requestObject.setArgs(args);
        //按版本请求就设置此参数
        requestObject.setVersion(version);

        //根据接口名称得到对应的服务地址
        String serviceAddress = serviceDiscovery.discover(requestObject.getClassName());
        System.out.println(serviceAddress);
        TCPTransport tcpTransport = new TCPTransport(serviceAddress);

        return tcpTransport.send(requestObject);
    }
}
