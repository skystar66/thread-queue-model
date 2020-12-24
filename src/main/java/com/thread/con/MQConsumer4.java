package com.thread.con;

import com.thread.con.room.LiveRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MQConsumer4 implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MQConsumer4.class);

    private ExecutorService executors = Executors.newFixedThreadPool(20);

    private ConcurrentLinkedQueue<LiveRoom> msgQueue;


    public MQConsumer4(int queueNum) {
        this.msgQueue = MQProvider.getFromRPCRoomMsgQueueByIndex(queueNum);
//        this.executors=executors;
        logger.info("当前队列 房间数量：" + msgQueue.size());
    }

    private void dumpQueue() {

        LiveRoom room = null;
        while ((room = msgQueue.poll()) != null) {
            LiveRoom finalPoll = room;

            finalPoll.batchSend();

        }


    }

    @Override
    public void run() {

        dumpQueue();

    }
}
