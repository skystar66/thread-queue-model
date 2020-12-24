package com.thread.con.masterwork;

import com.thread.con.room.LiveRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Worker implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Worker.class);
    private ConcurrentLinkedQueue<LiveRoom> workQueue;
    private CopyOnWriteArrayList<Object> resultMapList;


    public void setWorkQueue(ConcurrentLinkedQueue<LiveRoom> workQueue) {
        this.workQueue = workQueue;
    }

    public void setResultMap(CopyOnWriteArrayList<Object> resultMapList) {
        this.resultMapList = resultMapList;
    }


    public Worker(){
    }

    public void run() {
        while (true) {
            LiveRoom input = this.workQueue.poll();
            if (input == null) break;
            Object output = handle(input);
//            if (null != output) {
//                this.resultMapList.add(output);
//            }
        }
    }

    private Object handle(LiveRoom task) {
        try {
//            DBObject msgDB = findOne2(task.getValue(), task.getObject(), task.getTable());
            task.batchSend();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
