package com.thread.con.queue;

import com.thread.con.vo.Message;


/**
 * 返回线程池对象
 */
public class ThreadProviderBatchQueue {


    // 队列，
    public static BatchQueue<Message> batchQueue;

    static {

        batchQueue = new BatchQueue<>();
    }

    public static BatchQueue<Message> getBatchQueue() {

        return batchQueue;
    }


}
