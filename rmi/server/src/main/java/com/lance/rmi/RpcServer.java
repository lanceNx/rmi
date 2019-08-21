package com.lance.rmi;

import com.lance.rmi.anno.RegisterAnno;
import com.lance.rmi.zk.IRegisterCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {

    //创建一个线程池
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private IRegisterCenter registerCenter;
    private String address;

    private Map<String, Object> map = new HashMap();

    public RpcServer(IRegisterCenter registerCenter, String address) {
        this.registerCenter = registerCenter;
        this.address = address;
    }

    public void publisher() {
        ServerSocket serverSocket = null;
        try {
            //启动对应端口服务
            serverSocket = new ServerSocket(Integer.parseInt(address.split(":")[1]));
            //向zk注解绑定的服务
            for (String interfaceName : map.keySet()) {
                registerCenter.register(interfaceName, address);
            }

            while (true) {
                //这一方法可以说是阻塞式的,没有client端连接就一直监听着,等待连接
                Socket socket = serverSocket.accept();
                executorService.execute(new ProcessorHandler(socket, map));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void bind(Object... services) {
        for (Object service : services) {
            RegisterAnno annotation = service.getClass().getAnnotation(RegisterAnno.class);
            //获取服务名称
            String serviceName = annotation.value().getName();

            //获取注解上的版本
            String version = annotation.version();
            if (null != version && !"".equals(version)) {
                serviceName = serviceName + "-" + version;
            }
            //将服务名名称和服务实例绑定
            map.put(serviceName, service);
        }
    }
}
