//package com.thread.con;
//
//import com.thread.con.monitor.ThreadPoolMonitor;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ExecutorService;
//
//
///**
// * mq 处理任务执行类
// */
//public class MQRun {
//
//    //线程池max/core thread 数量
//    private static int threadPoolCount = MQProvider.threadCnt;
//
//    private static int roomCount = 2000;
//    private static int msgCount = 5000;
//
//
//    public static long time;
//
//    private static ExecutorService threadPool = null;
//
//    static Map<String, Thread> workers = new HashMap<>();
//
//
//    public static void main(String[] args) {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                zixuanMsg();
////            }
////        }).start();
//
//        //线程--队列
////        moniRoomMsg2();
////        moniRoomMsg4();
////        moniRoomMsg5();
////        threadPool =
////                    ThreadPoolMonitor.newFixedThreadPool(MQProvider.threadCnt,"excute-send-msg");
//////
////        long start = System.currentTimeMillis();
////        //定时
////        for (int i = 0; i < threadPoolCount; i++) {
////            threadPool.execute(new Runnable() {
////                @Override
////                public void run() {
////                    try {
////
////                        Thread.sleep(1000);
////                        System.out.println("1");
////                    }catch (Exception ex) {
////
////                    }
////                }
////            });
////        }
////        System.out.println("创建线程耗费时间："+(System.currentTimeMillis()-start)+"ms");
////
////        threadPool.shutdown();
////
////        while (true) {
////            if (threadPool.isTerminated()) {
////                System.out.println("线程执行耗时时间："+(System.currentTimeMillis()-start)+"ms");
////
////                break;
////            }
////        }
//
////
////
////
////
//////
//        for (int i=0;i<10;i++) {
//
//            //master worker 设计模式 653ms
//            masterWorker();
//            //定时任务               376ms
//            moniRoomMsg();
////            线程池队列              547ms
//            threadPool =
//                    ThreadPoolMonitor.newFixedThreadPool(MQProvider.threadCnt,"excute-send-msg");
//
//            moniRoomMsg4();
//        }
//        System.out.println("10 次请求 "+(roomCount*msgCount*10)+" 条消息， avg time："+time/10+"ms");
//
//////
//
//    }
//
//
//    /**
//     * master-worker 设计模式
//     */
//    public static void masterWorker() {
//
//        long start = System.currentTimeMillis();
//
//        /**初始化 master 任务 将会由worker 进行处理，worker 数量为 30个*/
//        Master master = new Master(new Worker(), com.liveme.demo.msgqueue.MQProvider.threadCnt);
//
//
//        //例如1000个直播间
//        for (int i = 0; i < roomCount; i++) {
//            LiveRoom liveRoom = RoomsDispatcher.getInstance().createRoom(String.valueOf(i));
//            /**没个直播间发送1000条消息*/
//            for (int j = 0; j < msgCount; j++) {
//                RoomsDispatcher.getInstance().add(String.valueOf(i),
//                        String.valueOf(i));
//            }
//            master.submit(liveRoom);
//        }
//
//        System.out.println("master-worker 设计模式 新自研线程模型 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
//        start = System.currentTimeMillis();
//
//        /**master 开始执行*/
//        master.execute();
//
//        while (true) {
//            /**判断所有worker 是否执行完成*/
//            if (master.isComplete()) {
//                long cost = (System.currentTimeMillis() - start);
//                System.out.println("master-worker 设计模式："+roomCount * msgCount + "条数据，新自研线程模型 ，执行耗时为: " + cost+ "ms");
//                master.detroy();
//                time=time+cost;
//                break;
//            }
//        }
//
//    }
//
//
//    public static boolean isComplete() {
//        for (Map.Entry<String, Thread> me : workers.entrySet()) {
//            if (me.getValue().getState() != Thread.State.TERMINATED) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//    /**
//     * 生成queue
//     */
//    public static ConcurrentLinkedQueue<String> createQueue() {
//        ConcurrentLinkedQueue<String> queue = com.liveme.demo.msgqueue.MQProvider.getFromRPCRoomMsgQueueByRandom();
//        for (int i = 0; i < 2500000; i++) {
//            queue.offer(String.valueOf(i));
//        }
//        return queue;
//    }
//
//
//    /**
//     * 模拟直播间消息
//     */
//    public static void moniRoomMsg() {
//
//        long start = System.currentTimeMillis();
//
//        //例如1000个直播间
//        for (int i = 0; i < roomCount; i++) {
//            LiveRoom liveRoom = RoomsDispatcher.getInstance().createRoom(String.valueOf(i));
////            liveRoom.monitor();
//            /**没个直播间发送1000条消息*/
//            for (int j = 0; j < msgCount; j++) {
//                RoomsDispatcher.getInstance().add(String.valueOf(i),
//                        String.valueOf(i));
//            }
//        }
////        System.out.println("定时 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
//        start = System.currentTimeMillis();
//        //开启定时任务
//        RoomsDispatcher.getInstance().init();
////        RoomsDispatcher.getInstance().batchSend();
//        while (true) {
//            if (StaticMessageRecord.atomicLong.get() >= roomCount * msgCount) {
//                final long end = System.currentTimeMillis();
//                long cost = (end - start);
//                time = time + cost;
//                System.out.println(roomCount * msgCount + " 条消息 定时 共耗时：" + cost + "ms");
//
////
//                RoomsDispatcher.getInstance().getScheduledExecutorService().shutdown();
//                RoomsDispatcher.getInstance().cleanRoom();
//                StaticMessageRecord.atomicLong.set(0);
//                break;
//            }
//        }
//    }
//
//
//    /**
//     * 自旋任务数
//     */
//    public static void zixuanMsg() {
//
//
//        while (true) {
//            try {
//
//                System.out.println("当前条数：" + StaticMessageRecord.atomicLong.get());
//                Thread.sleep(2000);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
//
//
//    /**
//     * 定时任务 N个 线程模型
//     */
//    public static void moniRoomMsg3() {
//
//        long start = System.currentTimeMillis();
//
//
//        //例如1000个直播间
//        for (int i = 0; i < 1000; i++) {
//
//            String roomId = UUID.randomUUID().toString().replace("-", "");
//
//            RoomsDispatcher.getInstance().createRoom(roomId);
//            /**没个直播间发送1000条消息*/
//            for (int j = 0; j < 1000; j++) {
//                RoomsDispatcher.getInstance().add(roomId, roomId);
//            }
//        }
//        System.out.println("生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
//
//        start = System.currentTimeMillis();
//        //开启定时任务
//        RoomsDispatcher.getInstance().init();
//        while (true) {
//            if (StaticMessageRecord.atomicLong.get() >= 1000 * 1000) {
//                final long end = System.currentTimeMillis();
//                System.out.println(1000 * 1000 + " 条消息 定时 共耗时：" + (end - start) + "ms");
//                break;
//            }
//        }
//    }
//
//    /**
//     * 自研线程模型
//     */
//    public static void moniRoomMsg2() {
//
//        long start = System.currentTimeMillis();
//        int queueCount = com.liveme.demo.msgqueue.MQProvider.threadCnt;
//
//        //生产消息 1000000 条消息
//        for (int i = 0; i < roomCount; i++) {
//            /**没个直播间发送1000条消息*/
//            for (int j = 0; j < msgCount; j++) {
//                com.liveme.demo.msgqueue.MQProvider.push(String.valueOf(i), String.valueOf(j));
//            }
//        }
//
////        System.out.println("自研 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
//
//        start = System.currentTimeMillis();
//        //30个线程消费消息
//        for (int i = 0; i < threadPoolCount; i++) {
//            threadPool.submit(new com.liveme.demo.msgqueue.MQConsumer2());
//        }
//        threadPool.shutdown();
//
//        while (true) {
//            if (threadPool.isTerminated()) {
//                final long end = System.currentTimeMillis();
//                System.out.println(roomCount * msgCount + " 条消息 自研 共耗时：" + (end - start) + "ms");
//                break;
//            }
//        }
//
//    }
//
//
//    /**
//     * 自研线程模型
//     */
//    public static void moniRoomMsg4() {
//
//        long start = System.currentTimeMillis();
//        int queueCount = com.liveme.demo.msgqueue.MQProvider.threadCnt;
//
//        //生产消息 1000000 条消息
//        for (int i = 0; i < roomCount; i++) {
//            /**没个直播间发送1000条消息*/
//            for (int j = 0; j < msgCount; j++) {
//                com.liveme.demo.msgqueue.MQProvider.push(String.valueOf(i), String.valueOf(j));
//            }
//        }
//
//        System.out.println("自研 生成消息 耗费时间：" + (System.currentTimeMillis() - start) + "ms");
//
//        start = System.currentTimeMillis();
//        //30个线程消费消息
//        for (int i = 0; i < threadPoolCount; i++) {
//            threadPool.execute(new com.liveme.demo.msgqueue.MQConsumer3(
//                    i % queueCount));
//        }
//        System.out.println("创建线程耗费时间："+(System.currentTimeMillis()-start)+"ms");
//        threadPool.shutdown();
//
//        while (true) {
//            if (threadPool.isTerminated()) {
//                final long end = System.currentTimeMillis();
//                long cost = (end - start);
//                System.out.println(roomCount * msgCount + " 条消息 自研 共耗时：" + cost + "ms");
//                time = time + cost;
//                break;
//            }
//        }
//
//    }
//
//    /**
//     * 自研线程模型
//     */
//    public static void moniRoomMsg5() {
//
//        Map<Integer, ConcurrentLinkedQueue<String>> map = new HashMap<>();
//
//        for (int i = 0; i < threadPoolCount; i++) {
//            map.put(i, createQueue());
//        }
//
//        long start = System.currentTimeMillis();
//
////        Thread thread = new Thread(new MQConsumer(queue));
////
////        thread.start();
////
////        while (true) {
////            if (thread.getState() == Thread.State.TERMINATED) {
////                System.out.println("耗时："+(System.currentTimeMillis()-start)+"ms");
////                break;
////            }
////        }
//
////
//        for (int i = 0; i < threadPoolCount; i++) {
//            Thread thread = new Thread(new com.liveme.demo.msgqueue.MQConsumer(map.get(i)));
//            thread.start();
//            workers.put("" + i, thread);
//
//        }
//
//        while (true) {
//            if (isComplete()) {
//                System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
//                break;
//            }
//        }
//
////
////        while (true) {
////            if (threadPool.isTerminated()) {
////                System.out.println("耗时："+(System.currentTimeMillis()-start)+"ms");
////                break;
////            }
////        }
//
//
//    }
//}
