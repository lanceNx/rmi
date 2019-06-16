package com.lance.rmi.zk;

public interface IServiceDiscovery {

    String discover(String serviceName) throws Exception;

}
