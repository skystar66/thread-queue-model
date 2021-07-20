package com.thread.con.disruptor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ArrayBlockingQueue4Test {


    public static void main(String[] args) {

    }




    public static void testAyyayQueue(){
        final ConcurrentLinkedQueue<Data> queue = new ConcurrentLinkedQueue<Data>();
        final long startTime = System.currentTimeMillis();
        // 向容器中添加元素
        new Thread(new Runnable() {
            @Override
            public void run() {
                long i = 0;
                while (i < Constants.EVENT_NUM_FM) {
                    try {
                        queue.offer(new Data(i, "c" + i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
        }).start();
        // 从容器中取出元素
        new Thread(new Runnable() {
            @Override
            public void run() {
                long k = 0;
                while (k < Constants.EVENT_NUM_FM) {
                    try {
                        queue.poll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    k++;
                }
                long endTime = System.currentTimeMillis();
                System.out.println("ConcurrentLinkedQueue Queue costTime = " + (endTime - startTime) + "ms");
            }
        }).start();
    }

}
