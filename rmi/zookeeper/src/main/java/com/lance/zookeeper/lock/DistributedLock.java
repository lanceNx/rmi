package com.lance.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class DistributedLock implements Lock, Watcher {

    private ZooKeeper zooKeeper = null;
    //设置锁根节点
    private String ROOT_LOCK = "/locks";
    private String WAIT_LOCK; //等待的前一个锁
    private String CURRENT_LOCK;//当前获取的锁

    public DistributedLock() {
        Stat stat = null;
        try {
            zooKeeper = new ZooKeeper(
                    "47.99.223.159:2181",
                    50000, this);
            //判断根节点是否存在
            stat = zooKeeper.exists(ROOT_LOCK, false);
            //如果不存在 返回null 就创建一个根节点
            if (null == stat) {
                zooKeeper.create(ROOT_LOCK + "/", "0".getBytes(),
                        OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //获取锁
    public void lock() {
        //判断获取锁成功
        if (this.tryLock()) {
            System.out.println(Thread.currentThread().getName()+" -> 获取锁成功");
            return;
        }




    }

    public void lockInterruptibly() throws InterruptedException {

    }

    //判断是否获取锁成功
    public boolean tryLock() {
        CURRENT_LOCK = zooKeeper.create(ROOT_LOCK + "/", "0".getBytes(),
                OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(Thread.currentThread().getName()+" -> 尝试获取锁 " + CURRENT_LOCK);
        List<String> children = zooKeeper.getChildren(ROOT_LOCK + "/", false);


        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    //释放锁
    public void unlock() {

    }

    public Condition newCondition() {
        return null;
    }

    //zookeeper节点信息更新回调方法
    public void process(WatchedEvent watchedEvent) {

    }
}
