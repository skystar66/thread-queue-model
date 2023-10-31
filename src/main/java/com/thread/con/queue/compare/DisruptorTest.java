package com.thread.con.queue.compare;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.thread.con.vo.MessageEvent;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/30 15:32:17
 */
public class DisruptorTest {
    public static int infoNum = 50000000;
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        int ringBufferSize = 65536; //数据缓冲区的大小 必须为2的次幂

        EventFactory<MessageEvent> factory = new EventFactory<MessageEvent>() {
            @Override
            public MessageEvent newInstance() {
                return new MessageEvent();
            }
        };

        /**
         *
         *  factory，定义的事件工厂
         *  ringBufferSize，环形队列RingBuffer的大小，必须是2的N次方
         *  ProducerType，生产者线程的设置，当你只有一个生产者线程时设置为 ProducerType.SINGLE，多个生产者线程ProducerType.MULTI
         *  waitStrategy，消费者的等待策略
         *
         */
        final Disruptor<MessageEvent> disruptor = new Disruptor<MessageEvent>(factory, ringBufferSize,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        DisruptorEventConsumer consumer = new DisruptorEventConsumer();
        disruptor.handleEventsWith(consumer);
        disruptor.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RingBuffer<MessageEvent> ringBuffer = disruptor.getRingBuffer();
                for (int i = 0; i < infoNum; i++) {
                    long seq = ringBuffer.next();
                    MessageEvent infoEvent = ringBuffer.get(seq);
                    infoEvent.setMsg("info" + i);
                    ringBuffer.publish(seq);
                }
            }
        }).start();
    }
}
