package com.thread.con.queue.linkedQueue;

import com.thread.con.vo.MessageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/30 16:04:49
 */
public class BlockQueueTest {

    public static int total = 0;
    public static volatile int sum = 0;
    public static long startTime = 0;
    private static final long FNV_32_INIT = 2166136261L;
    private static final int FNV_32_PRIME = 16777619;
    static Map<Integer, LinkedBlockingQueue<MessageEvent>> linkedBlockingQueueMap = new HashMap<>();

    static ExecutorService executors = null;

    private static CountDownLatch countDownLatch = null;

    public static CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public static void setCountDownLatch(CountDownLatch countDownLatch) {
        BlockQueueTest.countDownLatch = countDownLatch;
    }

    public static void init(int thread) {
        executors = Executors.newFixedThreadPool(thread);
        for (int t = 0; t < thread; t++) {
            LinkedBlockingQueue<MessageEvent> queue = new LinkedBlockingQueue<>();
            linkedBlockingQueueMap.put(t, queue);
            executors.execute(new BlockQueueConsumer(queue));
        }
    }


    public static void produceMsg(String uid, String msg) {
        int hashCode = getHashCode(uid);
        int index = hashCode % linkedBlockingQueueMap.size();
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setMsg(msg);
        try {
            linkedBlockingQueueMap.get(index).put(messageEvent);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void produceSingleMsg(String msg) {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setMsg(msg);
        try {
            linkedBlockingQueueMap.get(0).put(messageEvent);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        startTime = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            produceMsg(i + "", i + "");
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(total + "条,共耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }

}
