package com.thread.con.queue.disruptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/26 16:29:21
 */
public class SingleDisruptorManager {
    static Map<Integer, SingleDisruptor> singleDisruptorMap = new HashMap<>();
    private static final long FNV_32_INIT = 2166136261L;
    private static final int FNV_32_PRIME = 16777619;

    public static int total = 0;


    private static CountDownLatch countDownLatch = null;

    public static AtomicInteger count=new AtomicInteger(0);
    public static boolean stop=false;

    private static SingleDisruptor singleDisruptor;


    public static CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public static void setCountDownLatch(CountDownLatch countDownLatch) {
        SingleDisruptorManager.countDownLatch = countDownLatch;
    }

    public static void init(int num) {
        for (int i = 0; i < num; i++) {
            SingleDisruptor singleDisruptor = new SingleDisruptor();
            singleDisruptor.init();
            singleDisruptorMap.put(i, singleDisruptor);
        }
    }

    public static void pushMsg(String uid, String msg) {
        int hashCode = getHashCode(uid);
        int index = hashCode % singleDisruptorMap.size();
        singleDisruptorMap.get(index).produceData(msg);
    }

    public static void pushMsg(String msg) {
        singleDisruptorMap.get(0).produceData(msg);
    }


    public static int getHashCode(String origin) {

        final int p = FNV_32_PRIME;
        int hash = (int) FNV_32_INIT;
        for (int i = 0; i < origin.length(); i++) {
            hash = (hash ^ origin.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        hash = Math.abs(hash);

        return hash;
    }

    public static void main(String[] args) {
        total = 50000000;
        countDownLatch = new CountDownLatch(total);
        init(4);
        long startTime =System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
//            singleDisruptor.produceData(i + "");
            pushMsg(i + "",i + "");
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        while (!stop){}
        System.out.println(total + "条,共耗时:" + (System.currentTimeMillis() - startTime) + "ms");


    }

}
