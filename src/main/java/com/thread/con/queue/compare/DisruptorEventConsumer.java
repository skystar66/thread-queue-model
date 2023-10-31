package com.thread.con.queue.compare;

import com.lmax.disruptor.EventHandler;
import com.thread.con.vo.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/26 16:23:04
 */

public class DisruptorEventConsumer implements EventHandler<MessageEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DisruptorEventConsumer.class);
    private int cnt;
    private long startTime;

    public DisruptorEventConsumer() {
        this.startTime = System.currentTimeMillis();
    }
    @Override
    public void onEvent(MessageEvent messageEvent, long l, boolean b) throws Exception {
//        logger.info("consumer tid:{},msg:{}",Thread.currentThread().getId(), messageEvent.getMsg());
        try {
            cnt++;
            if (cnt == DisruptorTest.infoNum) {
                long endTime = System.currentTimeMillis();
                System.out.println(" 消耗时间： " + (endTime - startTime) + "毫秒");
            }

        }catch (Exception exception) {
        }
    }

}
