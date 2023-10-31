package com.thread.con.queue.linkedQueue;

import com.thread.con.vo.MessageEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/30 16:02:47
 */
public class BlockQueueConsumer implements Runnable{


    BlockingQueue<MessageEvent> queue;

    public BlockQueueConsumer(BlockingQueue<MessageEvent> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                queue.take();
                BlockQueueTest.getCountDownLatch().countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
