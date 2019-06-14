package com.lance.zookeeper.demo;

import com.lance.zookeeper.lock.DistributedLock;

import java.util.concurrent.CountDownLatch;

public class AppDemo {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    DistributedLock distributedLock = new DistributedLock();
                    distributedLock.lock();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
            countDownLatch.countDown();
        }

    }

}
