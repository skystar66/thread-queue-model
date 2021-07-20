package com.thread.con.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.thread.con.utils.ThreadPoolUtils;
import com.thread.con.vo.MessageEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 发送检测结果用的disruptor
 *
 * @author yzj
 * @date 2020-12-17 15:59
 */
public class DisruptorUtil {

    private static Disruptor<MessageEvent> disruptor;

    private static RingBuffer<MessageEvent> ringBuffer;


    private static ExecutorService executors = ThreadPoolUtils.getInstance().getExecutorService();

    private static final class ThreadPoolUtilsHold {
        private static final DisruptorUtil instance = new DisruptorUtil();
    }

    public static DisruptorUtil getInstance() {
        return DisruptorUtil.ThreadPoolUtilsHold.instance;
    }


    public DisruptorUtil() {
        init();
    }

    private CountDownLatch countDownLatch;

    public static long start;
    public static int count;


    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        start = System.currentTimeMillis();
    }

    public void produce(String msg) {
        ringBuffer.publishEvent((event, sequence, data) -> {
            event.setMsg(msg);

        });
    }


    /**
     * 消费者定义
     */
    private class Consumer implements EventHandler<MessageEvent> {

        @Override
        public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) throws Exception {
            // 发送信息到kafka入库
            try {

                count++;
                if (count == 1000000) {
                    System.out.println("1000000 条消息 distruptor queue 共耗时："
                            + (System.currentTimeMillis()-start) + "ms");


                }
                //                executors.submit(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            log.info("收到消息：{}ms",messageEvent.getMsg());
//                countDownLatch.countDown();
//                        }catch (Exception ex) {
//
//                        }
//                    }
//                });
            } catch (Exception e) {
            }
        }
    }


    public void init() {

        // 2 ^ 17
        int bufferSize = 131072;
//        disruptor = new Disruptor<>(MessageEvent::new, bufferSize,
//                DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new BlockingWaitStrategy());

        disruptor = new Disruptor<>(
                MessageEvent::new,
                bufferSize,
                Executors.newSingleThreadExecutor(),
                ProducerType.SINGLE,
                //new BlockingWaitStrategy()
                new YieldingWaitStrategy()
        );

        /*
        单个消费者实现
         */
        disruptor.handleEventsWith(new Consumer());
        disruptor.start();

        ringBuffer = disruptor.getRingBuffer();


    }
}
