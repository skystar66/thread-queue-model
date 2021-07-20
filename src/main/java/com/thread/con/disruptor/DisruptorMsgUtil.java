package com.thread.con.disruptor;//package com.bizseer.hubble.anomaly.utils.queue;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.thread.con.queue.MQProvider;
import com.thread.con.utils.ThreadPoolUtils;
import com.thread.con.vo.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 发送检测结果用的disruptor
 *
 * @author xl
 * @date 2021-04-03 15:59
 */
public class DisruptorMsgUtil {


    private static final Logger log = LoggerFactory.getLogger(DisruptorMsgUtil.class);

    private static final class ThreadPoolUtilsHold{
        private static final DisruptorMsgUtil instance = new DisruptorMsgUtil();
    }

    public static DisruptorMsgUtil getInstance(){
        return DisruptorMsgUtil.ThreadPoolUtilsHold.instance;
    }


    public DisruptorMsgUtil() {
        init();
    }


    private CountDownLatch countDownLatch;


    public void setCountDownLatch(CountDownLatch countDownLatch){
        this.countDownLatch=countDownLatch;
    }

    private static Disruptor<MessageEvent> disruptor;

    private static RingBuffer<MessageEvent> ringBuffer;

    private static ExecutorService executors = ThreadPoolUtils.getInstance().getExecutorService();

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

    private void takeQueue(MessageEvent messageEvent) {
            try {
                executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            log.info("收到消息：{}ms",messageEvent.getMsg());
                            countDownLatch.countDown();
                        }catch (Exception ex) {

                        }
                    }
                });
            } catch (Exception ex) {
                log.error("error:{}", ex);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                }
            }
    }


    /**
     * 消费者定义
     */
    private class DetectConsumer implements WorkHandler<MessageEvent> {

        @Override
        public void onEvent(MessageEvent messageEvent) throws Exception {
            try {
                takeQueue(messageEvent);
            } catch (Exception e) {
                log.error("detect exception when execute event: " + e.getMessage());
            }
        }
    }


    public void init() {

        // 指定 ring buffer字节大小，必需为2的N次方(能将求模运算转为位运算提高效率 )，否则影响性能
        int bufferSize = 1024 * 1024;
        //固定线程数
        int nThreads = 1;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        EventFactory<MessageEvent> factory = new EventFactory<MessageEvent>() {
            @Override
            public MessageEvent newInstance() {
                return new MessageEvent();
            }
        };
        // 创建ringBuffer
        ringBuffer = RingBuffer.create(ProducerType.SINGLE, factory, bufferSize,
            new YieldingWaitStrategy());
        SequenceBarrier barriers = ringBuffer.newBarrier();
        DetectConsumer[] consumers = new DetectConsumer[nThreads];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new DetectConsumer();
        }
        WorkerPool<MessageEvent> workerPool = new WorkerPool<MessageEvent>(ringBuffer, barriers,
            new EventExceptionHandler(), consumers);
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(executor);
    }




    public static class EventExceptionHandler implements ExceptionHandler {

        @Override
        public void handleEventException(Throwable ex, long sequence, Object event) {
            log.error("handleEventException：" + ex);
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            log.error("handleEventException：" + ex);
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            log.error("handleOnStartException：" + ex);
        }

    }
}
