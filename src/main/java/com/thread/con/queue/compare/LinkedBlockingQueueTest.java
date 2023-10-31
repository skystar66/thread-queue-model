package com.thread.con.queue.compare;

import com.thread.con.vo.MessageEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/30 15:31:27
 */
public class LinkedBlockingQueueTest {

    public static int infoNum = 50000000;

    public static void main(String[] args) {
        final BlockingQueue<MessageEvent> queue = new LinkedBlockingQueue<>();
        final long startTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int pcnt = 0;
                while (pcnt < infoNum) {
                    MessageEvent kafkaInfoEvent = new MessageEvent();
                    kafkaInfoEvent.setMsg(pcnt+"info");
                    try {
                        queue.put(kafkaInfoEvent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pcnt++;
                }
            }
        }).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                int cnt = 0;
                while (cnt < infoNum) {
                    try {
                        queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cnt++;
                }
                long endTime = System.currentTimeMillis();
                System.out.println("消耗时间： " + (endTime - startTime) + "毫秒");
            }
        }).start();
    }
}
