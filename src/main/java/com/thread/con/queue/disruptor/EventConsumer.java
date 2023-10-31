package com.thread.con.queue.disruptor;

import com.lmax.disruptor.EventHandler;
import com.thread.con.vo.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/26 16:23:04
 */

public class EventConsumer implements EventHandler<MessageEvent> {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private int cnt;
    private long startTime;

    public EventConsumer() {
        this.startTime = System.currentTimeMillis();
    }
    @Override
    public void onEvent(MessageEvent messageEvent, long l, boolean b) throws Exception {
        try {
            SingleDisruptorManager.getCountDownLatch().countDown();

//            if (SingleDisruptorManager.count.incrementAndGet()==SingleDisruptorManager.total){
//                SingleDisruptorManager.stop=true;
//            }
        }catch (Exception exception) {
        }
    }

}
