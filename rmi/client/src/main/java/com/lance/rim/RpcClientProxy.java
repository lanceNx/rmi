package com.lance.rim;

import java.lang.reflect.Proxy;

public class RpcClientProxy {

    public <T> T clientProxy(Class<T> clazz, String host, Integer port) {
        return (T)Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new RemoteInvocationHandler(host,port));
    }
}
