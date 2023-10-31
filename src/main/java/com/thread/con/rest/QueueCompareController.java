//package com.thread.con.rest;
//
//import com.thread.con.monitor.ThreadPoolMonitor;
//import com.thread.con.queue.concurrentLinkedQueue.MQConsumerMessageEvent;
//import com.thread.con.queue.MQProvider;
//import com.thread.con.queue.disruptor.SingleDisruptorManager;
//import com.thread.con.vo.MessageEvent;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.Duration;
//import java.util.UUID;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//
//@RestController
//@RequestMapping("queue")
//public class QueueCompareController {
//
//    private static final Logger logger = LoggerFactory.getLogger(QueueCompareController.class);
//
//    public long time;
//    public long time2;
//
//    private static int queueCount = MQProvider.threadCnt;
//
//
//    private static ExecutorService threadPool = null;
//    private static ExecutorService threadPoolExecute = null;
//
//
//    /**
//     * 自研 线程----队列 对外的qps吞吐量 消息处理设计方案
//     *
//     * @param loopC    循环次数
//     * @param msgCount 每个直播间消息数量
//     * @desc: 请求样例： http://127.0.0.1:11780/thread/zyQueue2?roomCount=1000&msgCount=1000&loopC=20
//     */
//    @RequestMapping(value = "zyQueue", method = RequestMethod.GET)
//    public String zyQueue2(
//            @RequestParam("msgCount") int msgCount,
//            @RequestParam("loopC") int loopC) {
//
//        //初始化time 为0
//        time = 0;
//        for (int k = 0; k < loopC; k++) {
//            threadPool = ThreadPoolMonitor.
//                    newFixedThreadPool(MQProvider.threadCnt, "excute-send-msg");
//            long start = System.currentTimeMillis();
//            //生产消息 msgCount*roomCount 条消息
//            /**每个直播间发送msgCount条消息*/
//            for (int j = 0; j < msgCount; j++) {
//                MessageEvent messageEvent = new MessageEvent();
//                messageEvent.setMsg(UUID.randomUUID().toString().replace("-", ""));
//                MQProvider.pushMessageEvent(messageEvent);
//            }
////            logger.info("自研 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
////            start = System.currentTimeMillis();
//            CountDownLatch countDownLatch = new CountDownLatch(msgCount);
//            //30个线程消费消息
//            for (int i = 0; i < MQProvider.threadCnt; i++) {
//                threadPool.execute(new MQConsumerMessageEvent(
//                        i, countDownLatch));
//            }
//            threadPool.shutdown();
//            try {
//                countDownLatch.await();
//                long end = System.currentTimeMillis();
//                long cost = (end - start);
//                logger.info("第" + (k + 1) + "次，" + msgCount + " 条消息 原生queue 共耗时：" + cost + "ms");
//                time = time + cost;
//            } catch (Exception ex) {
//
//            }
//        }
//        logger.info(loopC + "次请求 " + (loopC * msgCount) + " 条消息， avg time：" + time / loopC + "ms");
//
//        return "CPU: " + MQProvider.threadCnt + "，" + loopC + " 次请求 " + (loopC * msgCount) + " 条消息， avg time：" + time / loopC + "ms";
//
//    }
//
//    /**
//     * disruptorQueue 队列 对外的qps吞吐量 消息处理设计方案
//     *
//     * @param loopC    循环次数
//     * @param msgCount 每个直播间消息数量
//     * @desc: 请求样例： http://127.0.0.1:11780/thread/zyQueue2?roomCount=1000&msgCount=1000&loopC=20
//     */
//    @RequestMapping(value = "disruptorQueue", method = RequestMethod.GET)
//    public String disruptorQueue(
//            @RequestParam("msgCount") int msgCount,
//            @RequestParam("loopC") int loopC) {
//
//        //初始化time 为0
//        time = 0;
//        for (int k = 0; k < loopC; k++) {
//            long start = System.currentTimeMillis();
//            CountDownLatch countDownLatch = new CountDownLatch(msgCount);
//            SingleDisruptorManager.setCountDownLatch(countDownLatch);
//            SingleDisruptorManager.init(1);
//            //生产消息 msgCount*roomCount 条消息
//            /**每个直播间发送msgCount条消息*/
//            for (int j = 0; j < msgCount; j++) {
//                SingleDisruptorManager.pushMsg(j+"", j + "");
//            }
//            try {
//                countDownLatch.await();
//                long end = System.currentTimeMillis();
//                long cost = (end - start);
//                logger.info("第" + (k + 1) + "次，threadCount:" + MQProvider.threadCnt + "," + msgCount + " 条消息 distruptor queue 共耗时：" + cost + "ms");
//                time = time + cost;
//            } catch (Exception ex) {
//
//            }
//        }
//        logger.info(loopC + "次请求 " + (loopC * msgCount) + " 条消息， avg time：" + time / loopC + "ms");
//        return "CPU: " + MQProvider.threadCnt + "，" + loopC + " 次请求 " + (loopC * msgCount) + " 条消息， avg time：" + time / loopC + "ms";
//    }
//
//
//    /**
//     * 自研 线程----队列 对外的qps吞吐量 消息处理设计方案
//     *
//     * @param loopC    循环次数
//     * @param msgCount 每个直播间消息数量
//     * @desc: 请求样例： http://127.0.0.1:11780/thread/zyQueue2?roomCount=1000&msgCount=1000&loopC=20
//     */
//    @RequestMapping(value = "linkedQueue", method = RequestMethod.GET)
//    public String linkedQueue(
//            @RequestParam("msgCount") int msgCount,
//            @RequestParam("loopC") int loopC) {
//
//        //初始化time 为0
//        time = 0;
//        for (int k = 0; k < loopC; k++) {
//            threadPool = ThreadPoolMonitor.
//                    newFixedThreadPool(LinkedMQProvider.threadCnt, "excute-send-msg");
//            //生产消息 msgCount*roomCount 条消息
//            /**每个直播间发送msgCount条消息*/
//            for (int j = 0; j < msgCount; j++) {
//                MessageEvent messageEvent = new MessageEvent();
//                messageEvent.setMsg(UUID.randomUUID().toString().replace("-", ""));
//                LinkedMQProvider.getToRPCMsgQueueByRandom(messageEvent.getMsg()).push(messageEvent, Duration.ofMillis(100));
//            }
//            long start = System.currentTimeMillis();
//            CountDownLatch countDownLatch = new CountDownLatch(msgCount);
//            //30个线程消费消息
//            for (int i = 0; i < LinkedMQProvider.threadCnt; i++) {
//                threadPool.execute(new LinkedQueueConsumer(
//                        i % queueCount, countDownLatch));
//            }
//            threadPool.shutdown();
//            try {
//                countDownLatch.await();
//                long end = System.currentTimeMillis();
//                long cost = (end - start);
//                logger.info("threadCount:"+LinkedMQProvider.threadCnt+"第" + (k + 1) + "次，" + msgCount + " 条消息, 原生queue 共耗时：" + cost + "ms");
//                time = time + cost;
//            } catch (Exception ex) {
//            }
//        }
//        logger.info(loopC + "次请求 " + (loopC * msgCount) + " 条消息， avg time：" + time / loopC + "ms");
//        return "CPU: " + LinkedMQProvider.threadCnt + "，" + loopC + " 次请求 " + (loopC * msgCount) + " 条消息， avg time：" + time / loopC + "ms";
//
//    }
//
//
//}
