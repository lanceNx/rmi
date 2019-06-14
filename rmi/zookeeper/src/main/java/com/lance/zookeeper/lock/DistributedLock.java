package com.lance.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
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

    private CountDownLatch countDownLatch;

    public DistributedLock() {
        try {
            zooKeeper = new ZooKeeper(
                    "47.99.223.159:2181",
                    50000, this);
            //判断根节点是否存在
            Stat stat = zooKeeper.exists(ROOT_LOCK, false);
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
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+"->获得锁成功");
            return;
        }

        try {
            //没有获取到锁，就进行等待上一个节点释放锁
            waitForLock(WAIT_LOCK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean waitForLock(String wait_lock) throws Exception {
        Stat stat = zooKeeper.exists(wait_lock, true);
        if(null != stat) {
            System.out.println(Thread.currentThread().getName()+" 线程等待 -> "+wait_lock+" 释放");
            //设置一个等待位置
            countDownLatch = new CountDownLatch(1);
            //线程阻塞 如果该线程唤醒 就表示获取锁成功
            countDownLatch.await();
            //TODO watcher 触发后，还需在此判断等待节点是不是最小节点
            System.out.println(Thread.currentThread().getName()+" 线程 -> 获取锁成功 ");
        }
        //如果上一个节点不存在，就表示获取锁成功
        return true;
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    //判断是否获取锁成功
    public boolean tryLock() {
        try {
            //创建临时顺序节点
            CURRENT_LOCK = zooKeeper.create(ROOT_LOCK + "/", "0".getBytes(),
                    OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName()+"->"+
                    CURRENT_LOCK+"，尝试竞争锁");
            //查询根节点下所有节点
            List<String> children = zooKeeper.getChildren(ROOT_LOCK, false);
            //对节点进行排序
            SortedSet set = new TreeSet();
            children.forEach(node -> {
                set.add(ROOT_LOCK + "/" + node);
            });
            //判断当前节点是否是最小的节点
            if(CURRENT_LOCK.equals(set.first())) {
                return true;
            }
            //如果上面有没有获取到锁 就查询出set中小于当前节点的节点
            SortedSet<String> leeThenMe = ((TreeSet) set).headSet(CURRENT_LOCK);

            //如果小于当前节点的节点
            if(!leeThenMe.isEmpty()) {
                //设置需要等待的节点为这个节点的上一个节点
                WAIT_LOCK = leeThenMe.last();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    //释放锁
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+" 释放锁"+ CURRENT_LOCK);
        try {
            //删除节点
            zooKeeper.delete(CURRENT_LOCK,-1);
            //设置当前节点为空
            CURRENT_LOCK = null;
            //关闭连接
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    public Condition newCondition() {
        return null;
    }

    //zookeeper节点信息更新回调方法
    public void process(WatchedEvent watchedEvent) {
        if(null != this.countDownLatch) {
            countDownLatch.countDown();
        }
    }
}
