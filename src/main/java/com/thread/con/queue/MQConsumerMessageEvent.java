package com.thread.con.queue;

import com.thread.con.room.LiveRoom;
import com.thread.con.utils.ThreadPoolUtils;
import com.thread.con.vo.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MQConsumerMessageEvent implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MQConsumerMessageEvent.class);

    private ExecutorService executors = ThreadPoolUtils.getInstance().getExecutorService();

    private ConcurrentLinkedQueue<MessageEvent> msgQueue;
    private CountDownLatch countDownLatch;


    public MQConsumerMessageEvent(int queueNum, CountDownLatch countDownLatch) {
        this.msgQueue = MQProvider.getFromRPCMessageEventMsgQueueByIndex(queueNum);
        this.countDownLatch = countDownLatch;
    }

    private void dumpQueue() {

        MessageEvent messageEvent = null;
        while ((messageEvent = msgQueue.poll()) != null) {
            executors.execute(new Runnable() {
                @Override
                public void run() {

                    countDownLatch.countDown();
                }
            });
        }
    }

    @Override
    public void run() {

        dumpQueue();

    }
}
