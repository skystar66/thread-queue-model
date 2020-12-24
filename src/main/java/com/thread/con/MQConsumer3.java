package com.thread.con;

import com.thread.con.monitor.ThreadPoolMonitor;
import com.thread.con.room.LiveRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class MQConsumer3 implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MQConsumer3.class);

    private ExecutorService executors = ThreadPoolUtils.getInstance().getExecutorService();

    private ConcurrentLinkedQueue<LiveRoom> msgQueue;


    public MQConsumer3(int queueNum) {
        this.msgQueue = MQProvider.getFromRPCRoomMsgQueueByIndex(queueNum);
//        this.executors=executors;
        logger.info("当前队列 房间数量："+msgQueue.size());
    }

    private void dumpQueue() {

        LiveRoom room = null;
        while ((room = msgQueue.poll()) != null) {
            LiveRoom finalPoll = room;
            executors.submit(new Runnable() {
                @Override
                public void run() {
                    finalPoll.batchSend();
                }
            });
        }
//        executors.shutdown();


    }

    @Override
    public void run() {

        dumpQueue();

    }
}
