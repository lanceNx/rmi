package com.lance.rim;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProcessorHandler implements Runnable {
    private Socket socket;
    private Map<String, Object> map = new HashMap();

    public ProcessorHandler(Socket socket, Map<String, Object> map) {
        this.socket = socket;
        this.map = map;
    }

    public void run() {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            RequestObject requestObject = (RequestObject) objectInputStream.readObject();
            Object result = invoke(requestObject);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != objectInputStream) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object invoke(RequestObject requestObject) throws Exception {
        Object[] args = requestObject.getArgs();
        Class[] types = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        String className = requestObject.getClassName();
        //从内存map中获取这个服务名称对应的服务实例
        Object service = map.get(className);
        Method method = service.getClass().getMethod(requestObject.getMethodName(), types);
        return method.invoke(service, args);
    }
}
