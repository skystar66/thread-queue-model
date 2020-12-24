package com.thread.con.room;

import com.thread.con.Constants;
import com.thread.con.result.StaticMessageRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wenlong on 2020/12/16 3:58 下午
 */
public class LiveRoom {

    private String redisKey;
    private String roomId;
    private String createId;
    private String appId;


    private static ExecutorService executors = Executors.newFixedThreadPool(10);

    private static final Integer MaxTake = 500;

    private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

    private static Logger log = LoggerFactory.getLogger(LiveRoom.class);


    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public boolean add(String msg) {
        return messages.add(msg);
    }

    private void dumpQueue() {
        String poll = null;
        int take = 0;
        while ((poll = messages.poll()) != null) {
            take++;
            if (take >= Constants.QUEUE_SIZE) {
                int finalTake = take;
                take=0;
//                System.out.println("send batch msg size:"+finalTake+"条");
                executors.execute(new Runnable() {
                    @Override
                    public void run() {
                        StaticMessageRecord.atomicLong.addAndGet(finalTake);
                    }
                });

            }
        }
    }

    private List<String> dumpQueue2() {
        String poll = null;
        List<String> tempArray = new ArrayList<>();
        int take = 0;
        while ((poll = messages.poll()) != null) {
            tempArray.add(poll);
            take++;

            if (take >= Constants.QUEUE_SIZE) {
                StaticMessageRecord.atomicLong.addAndGet(take);
//                dumpQueue();
                break;
            }
        }
        return tempArray;
    }


    public void batchSend() {

       this.dumpQueue();


//        System.out.println(Thread.currentThread().getName()+"---roomId:"+roomId+" -----send msg size:"+list.size());



    }

    public void batchSend2() {

        List<String> list = this.dumpQueue2();

        if (list.size() == 0) {
            return;
        }
//        System.out.println(Thread.currentThread().getName()+"---roomId:"+roomId+" -----send msg size:"+list.size());



    }

    /**监控房间队列数*/
    public void monitor(){

        new Thread(new Runnable() {
            @Override
            public void run() {

               while (true) {
                   try {
//                       System.out.println("roomId:"+roomId+"当前房间消息数："+messages.size()+"个！！");
                       Thread.sleep(3000);
                   }catch (Exception ex) {

                   }
               }
            }
        }).start();
    }
}
