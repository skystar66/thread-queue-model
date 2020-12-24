package com.thread.con.masterwork;


import com.thread.con.room.LiveRoom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Master {

    private ConcurrentLinkedQueue<LiveRoom> workQueue = new ConcurrentLinkedQueue<LiveRoom>();

    private HashMap<String, Thread> workers = new HashMap<String, Thread>();

    private CopyOnWriteArrayList<Object> resultMapList = new CopyOnWriteArrayList<>();


    public Master(Worker worker, int workerCount) {
        worker.setWorkQueue(this.workQueue);
        worker.setResultMap(this.resultMapList);
        for (int i = 0; i < workerCount; i++) {
            this.workers.put(Integer.toString(i), new Thread(worker));
        }
    }

    //提交任务
    public void submit(LiveRoom task) {
        this.workQueue.add(task);
    }

    //启动所有的worker方法去执行任务
    public void execute() {
        for (Map.Entry<String, Thread> me : workers.entrySet()) {
            me.getValue().start();
        }


    }

    //判断是否运行结束的方法
    public boolean isComplete() {
        for (Map.Entry<String, Thread> me : workers.entrySet()) {
            if (me.getValue().getState() != Thread.State.TERMINATED) {
                return false;
            }
        }
        return true;
    }

    //获取结果
    public List<Object> getResult() {
        return resultMapList;
    }

//    //计算结果方法
//    public String getTime() {
//        String time = "";
//        for (Map.Entry<String, Object> me : resultMap.entrySet()) {
//            if (me.getKey() != null) {
//                time = me.getKey();
//                break;
//            }
//        }
//        return time;
//    }

    //destory
    public void detroy() {
        for (Map.Entry<String, Thread> me : workers.entrySet()) {
            me.getValue().interrupt();
        }


    }

}
