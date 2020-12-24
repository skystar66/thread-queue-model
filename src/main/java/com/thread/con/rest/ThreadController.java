package com.thread.con.rest;

import com.thread.con.queue.MQConsumer3;
import com.thread.con.queue.MQConsumer4;
import com.thread.con.queue.MQProvider;
import com.thread.con.masterwork.Master;
import com.thread.con.masterwork.Worker;
import com.thread.con.monitor.ThreadPoolMonitor;
import com.thread.con.result.StaticMessageRecord;
import com.thread.con.room.LiveRoom;
import com.thread.con.room.RoomsDispatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("thread-queue")
public class ThreadController {


    public long time;
    public long time2;

    private static int queueCount = MQProvider.threadCnt;


    private static ExecutorService threadPool = null;
    private static ExecutorService threadPoolExecute = null;


    /**
     * 定时任务 消息处理设计方案
     *
     * @param loopC     循环次数
     * @param msgCount  每个直播间消息数量
     * @param roomCount 直播间数量
     */
    @RequestMapping(value = "time",method = RequestMethod.GET)
    public String time(@RequestParam("roomCount") int roomCount,
                       @RequestParam("msgCount") int msgCount,
                       @RequestParam("loopC") int loopC) {
        time = 0;

        for (int k = 0; k < loopC; k++) {
            long start = System.currentTimeMillis();
            //例如1000个直播间
            for (int i = 0; i < roomCount; i++) {
                LiveRoom liveRoom = RoomsDispatcher.getInstance().createRoom(String.valueOf(i));
//            liveRoom.monitor();
                /**每个直播间发送1000条消息*/
                for (int j = 0; j < msgCount; j++) {
                    RoomsDispatcher.getInstance().add(String.valueOf(i),
                            String.valueOf(j));
                }
            }
//        System.out.println("定时 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();
            //开启定时任务
            RoomsDispatcher.getInstance().init();
            //单线程推送
//        RoomsDispatcher.getInstance().batchSend();
            while (true) {
                if (StaticMessageRecord.atomicLong.get() >= roomCount * msgCount) {
                    final long end = System.currentTimeMillis();
                    long cost = (end - start);
                    time = time + cost;
                    System.out.println(roomCount * msgCount + " 条消息 定时 共耗时：" + cost + "ms");
                    RoomsDispatcher.getInstance().getScheduledExecutorService().shutdown();
                    RoomsDispatcher.getInstance().cleanRoom();
                    StaticMessageRecord.atomicLong.set(0);
                    break;
                }
            }
        }
        System.out.println("10 次请求 " + (roomCount * msgCount * 10) + " 条消息， avg time：" + time / loopC + "ms");
        return loopC + " 次请求 " + (roomCount * msgCount * 10) + " 条消息， avg time：" + time / loopC + "ms";
    }


    /**
     * 自研 线程---队列 对内的消息处理吞吐量 消息处理设计方案
     *
     * @param loopC     循环次数
     * @param msgCount  每个直播间消息数量
     * @param roomCount 直播间数量
     * @desc: 请求样例：http://127.0.0.1:11780/thread/zyQueue?roomCount=1000&msgCount=1000&loopC=20
     */
    @RequestMapping(value = "zyQueue",method = RequestMethod.GET)
    public String zyQueue(@RequestParam("roomCount") int roomCount,
                          @RequestParam("msgCount") int msgCount,
                          @RequestParam("loopC") int loopC) {
        //初始化time为0
        time = 0;
        time2 = 0;
        for (int k = 0; k < loopC; k++) {
            threadPool = ThreadPoolMonitor.
                    newFixedThreadPool(MQProvider.threadCnt, "excute-send-msg");
            long start = System.currentTimeMillis();
            //生产消息 1000000 条消息
            for (int i = 0; i < roomCount; i++) {
                LiveRoom room = RoomsDispatcher.getInstance().createRoom(String.valueOf(i));
                /**每个直播间发送1000条消息*/
                for (int j = 0; j < msgCount; j++) {
                    RoomsDispatcher.getInstance().add2(room, "" + j);
                }
                MQProvider.push(room, String.valueOf(i));
            }
            System.out.println("自研 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();
            //30个线程消费消息
            for (int i = 0; i < MQProvider.threadCnt; i++) {
                threadPool.submit(new MQConsumer3(
                        i % queueCount));
            }
            threadPool.shutdown();
            long finalStart = start;
            while (true) {
                if (StaticMessageRecord.atomicLong.get() >= roomCount * msgCount) {
                    final long end = System.currentTimeMillis();
                    long cost = (end - finalStart);
                    time2 = time2 + cost;
                    System.out.println(roomCount * msgCount + " 条消息 batchSend 共耗时：" + cost + "ms");
                    RoomsDispatcher.getInstance().cleanRoom();
                    StaticMessageRecord.atomicLong.set(0);
//                    threadPoolExecute=null;
//                    threadPool=null;
                    break;
                }
            }
            try {
                Thread.sleep(2000);
            } catch (Exception ex) {

            }
        }
        System.out.println(loopC + " 次请求 " + (roomCount * msgCount * loopC) + " 条消息， avg time：" + time2 / loopC + "ms");
        return loopC + " 次请求 " + (roomCount * msgCount * loopC) + " 条消息， avg time：" + time2 / loopC + "ms";
    }


    /**
     * 自研 线程----队列 对外的qps吞吐量 消息处理设计方案
     *
     * @param loopC     循环次数
     * @param msgCount  每个直播间消息数量
     * @param roomCount 直播间数量
     * @desc: 请求样例： http://127.0.0.1:11780/thread/zyQueue2?roomCount=1000&msgCount=1000&loopC=20
     */
    @RequestMapping(value = "zyQueue2",method = RequestMethod.GET)
    public String zyQueue2(@RequestParam("roomCount") int roomCount,
                           @RequestParam("msgCount") int msgCount,
                           @RequestParam("loopC") int loopC) {

        //初始化time 为0
        time = 0;
        for (int k = 0; k < loopC; k++) {
            threadPool = ThreadPoolMonitor.
                    newFixedThreadPool(MQProvider.threadCnt, "excute-send-msg");
            long start = System.currentTimeMillis();
            //生产消息 msgCount*roomCount 条消息
            for (int i = 0; i < roomCount; i++) {
                LiveRoom room = RoomsDispatcher.getInstance().createRoom(String.valueOf(i));
                /**每个直播间发送msgCount条消息*/
                for (int j = 0; j < msgCount; j++) {
                    RoomsDispatcher.getInstance().add2(room, "" + j);
                }
                MQProvider.push(room, String.valueOf(i));
            }
            System.out.println("自研 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();
            //30个线程消费消息
            for (int i = 0; i < MQProvider.threadCnt; i++) {
                threadPool.execute(new MQConsumer4(
                        i % queueCount));
            }
            threadPool.shutdown();
            while (true) {
                if (threadPool.isTerminated()) {
                    final long end = System.currentTimeMillis();
                    long cost = (end - start);
                    System.out.println(roomCount * msgCount + " 条消息 自研 共耗时：" + cost + "ms");
                    time = time + cost;
                    break;
                }
            }
        }
        System.out.println(loopC + "次请求 " + (roomCount * msgCount * 10) + " 条消息， avg time：" + time / loopC + "ms");
        return loopC + " 次请求 " + (roomCount * msgCount * 10) + " 条消息， avg time：" + time / loopC + "ms";
    }


    /**
     * 自研 master-worker 消息处理设计方案
     *
     * @param loopC     循环次数
     * @param msgCount  每个直播间消息数量
     * @param roomCount 直播间数量
     * @desc: 请求样例：http://127.0.0.1:11780/thread/master?roomCount=2000&msgCount=5000&loopC=20
     */
    @RequestMapping(value = "master",method = RequestMethod.GET)
    public String master(@RequestParam("roomCount") int roomCount,
                         @RequestParam("msgCount") int msgCount,
                         @RequestParam("loopC") int loopC) {

        //初始化time为0
        time = 0;
        for (int k = 0; k < loopC; k++) {
            long start = System.currentTimeMillis();
            /**初始化 master 任务 将会由worker 进行处理，worker 数量为 30个*/
            Master master = new Master(new Worker(), MQProvider.threadCnt);
            //例如1000个直播间
            for (int i = 0; i < roomCount; i++) {
                LiveRoom liveRoom = RoomsDispatcher.getInstance().createRoom(String.valueOf(i));
                /**没个直播间发送1000条消息*/
                for (int j = 0; j < msgCount; j++) {
                    RoomsDispatcher.getInstance().add(String.valueOf(i),
                            String.valueOf(i));
                }
                master.submit(liveRoom);
            }

            System.out.println("master-worker 设计模式 新自研线程模型 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();
            /**master 开始执行*/
            master.execute();
            while (true) {
                /**判断所有worker 是否执行完成*/
                if (master.isComplete()) {
                    long cost = (System.currentTimeMillis() - start);
                    System.out.println("master-worker 设计模式：" + roomCount * msgCount + "条数据，新自研线程模型 ，执行耗时为: " + cost + "ms");
                    master.detroy();
                    time = time + cost;
                    break;
                }
            }
        }
        System.out.println("10 次请求 " + (roomCount * msgCount * 10) + " 条消息， avg time：" + time / loopC + "ms");
        return loopC + " 次请求 " + (roomCount * msgCount * 10) + " 条消息， avg time：" + time / loopC + "ms";
    }


}
