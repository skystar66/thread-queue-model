//package com.thread.con;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class MQConsumer2 implements Runnable {
//
//
//    private static ExecutorService executors = Executors.newFixedThreadPool(10);
//
//    private ConcurrentLinkedQueue<String> msgQueue;
//
//    //queue 的序号
//    private int queueNum;
//
//    private int threadSeqNum;
//
//
//
//    private void dumpQueue(ConcurrentLinkedQueue<String> msgQueue) {
//        String poll = null;
//        List<String> tempArray = new ArrayList<>();
//        while ((poll = msgQueue.poll()) != null ){
//            tempArray.add(poll);
//        }
//    }
//
//    @Override
//    public void run() {
//        for (ConcurrentLinkedQueue<String> value : MQProvider.rPCRoomMsgQueueMap.values()) {
//            dumpQueue(value);
//        }
//    }
//}
