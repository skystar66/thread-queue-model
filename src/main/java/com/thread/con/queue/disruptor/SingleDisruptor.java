package com.thread.con.queue.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.thread.con.disruptor.CommandEventGcHandler;
import com.thread.con.vo.MessageEvent;

import java.util.concurrent.CountDownLatch;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/26 16:19:44
 */
public class SingleDisruptor {

    public static int total = 500000;

    private RingBuffer<MessageEvent> ringBuffer;


    public static CountDownLatch countDownLatch;

    public void produceData(String msg) {
        long sequence = ringBuffer.next(); // 获得下一个Event槽的下标
        try {
            // 给Event填充数据
            MessageEvent event = ringBuffer.get(sequence);
            event.setMsg(msg);
        } finally {
            // 发布Event，激活观察者去消费， 将sequence传递给该消费者
            // 注意，最后的 ringBuffer.publish() 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
            ringBuffer.publish(sequence);
        }
    }

    public void init() {
        // 指定 ring buffer字节大小，必需为2的N次方(能将求模运算转为位运算提高效率 )，否则影响性能
        int bufferSize = 65536;
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
        final Disruptor<MessageEvent> disruptor = new Disruptor<MessageEvent>(factory, bufferSize,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        disruptor.handleEventsWith(new EventConsumer())
                .then(new CommandEventGcHandler());

        // 启动disruptor线程
        disruptor.start();

        // 获取ringbuffer环，用于接取生产者生产的事件
        ringBuffer = disruptor.getRingBuffer();
    }


    public static void main(String[] args) {
        SingleDisruptor singleDisruptor = new SingleDisruptor();
        singleDisruptor.init();
        total = 5000000;
        for (int i = 0; i < total; i++) {
            singleDisruptor.produceData(i + "");
        }




    }


}
