package com.thread.con.queue;

import com.thread.con.utils.ThreadPoolUtils;
import com.thread.con.room.LiveRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;


public class MQConsumer3 implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MQConsumer3.class);

    private ExecutorService executors = ThreadPoolUtils.getInstance().getExecutorService();

    private ConcurrentLinkedQueue<LiveRoom> msgQueue;


    /**
     * @param index 监控对列编号索引
     * */
    public MQConsumer3(int index) {
        this.msgQueue = MQProvider.getFromRPCRoomMsgQueueByIndex(index);
//        this.executors=executors;
        logger.info("当前队列 房间数量："+msgQueue.size());
    }

    private void takeQueue() {

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

        takeQueue();

    }
}
