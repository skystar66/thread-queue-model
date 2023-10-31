package com.thread.con.queue.concurrentLinkedQueue;

import com.thread.con.queue.MQProvider;
import com.thread.con.utils.BussinessExecuteCostTimeUtils;
import com.thread.con.utils.ThreadPoolUtils;
import com.thread.con.vo.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;


public class MQConsumerMessageEvent implements Runnable {
    private ConcurrentLinkedQueue<MessageEvent> msgQueue;
    private CountDownLatch countDownLatch;
    public MQConsumerMessageEvent(int queueNum, CountDownLatch countDownLatch) {
        this.msgQueue = MQProvider.getFromRPCMessageEventMsgQueueByIndex(queueNum);
        this.countDownLatch = countDownLatch;
    }
    private void dumpQueue() {
        MessageEvent messageEvent = null;
        while ((messageEvent = msgQueue.poll()) != null) {
            try {
                Thread.sleep(BussinessExecuteCostTimeUtils.costTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            countDownLatch.countDown();
        }
    }
    @Override
    public void run() {
        dumpQueue();
    }
}
