package com.lance.rim;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPTransport {

    private String host;
    private Integer port;

    public TCPTransport(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    //创建一个socket连接
    public Socket createClientScoket() {
        System.out.println("创建一个新的连接");
        Socket socket;
        try {
            socket = new Socket(this.host, this.port);
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("连接建立失败");
        }
    }


    public Object send(RequestObject requestObject) {
        Socket socket = null;
        try {
            socket = createClientScoket();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    socket.getOutputStream());
            objectOutputStream.writeObject(requestObject);
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(
                    socket.getInputStream());
            Object result = objectInputStream.readObject();
            objectInputStream.close();
            objectOutputStream.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("发起远程调用异常:", e);
        } finally {
            if (null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
