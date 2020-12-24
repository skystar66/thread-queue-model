package com.thread.con;


import com.thread.con.room.LiveRoom;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class MQProvider {

    public static final int threadCnt = Runtime.getRuntime().availableProcessors()*2;//队列数量


    public static final Map<Integer, ConcurrentLinkedQueue<LiveRoom>> rPCRoomMsgQueueMap = new HashMap<>();


    static {
        for (int i = 0; i < threadCnt; i++) {
            rPCRoomMsgQueueMap.put(i,
                    new ConcurrentLinkedQueue<>());
        }
    }

    /**
     * 得到与index相匹配的队列
     *
     * @param index
     * @return
     */
    public static ConcurrentLinkedQueue<LiveRoom> getFromRPCRoomMsgQueueByIndex(int index) {
        return rPCRoomMsgQueueMap.get(index);
    }

    /**
     * 得到与key 取模的队列
     *
     * @param key
     * @return
     */
    public static ConcurrentLinkedQueue<LiveRoom> getFromRPCRoomMsgQueueByKey(int key) {
        int index = key % threadCnt;
        return rPCRoomMsgQueueMap.get(index);
    }

    /**
     * 得到随机的队列
     *
     * @return
     */
    public static ConcurrentLinkedQueue<LiveRoom>  getFromRPCRoomMsgQueueByRandom() {
        return rPCRoomMsgQueueMap.get(RandomUtils.nextInt(0, threadCnt));
    }

    /**
     * push 消息
     *
     * @param msg
     */
    public static void push(LiveRoom msg) {
        if (null != msg) {
            getFromRPCRoomMsgQueueByRandom()
                    .offer(msg);
        }
    }


    /**
     * push 消息
     *
     * @param msg
     */
    public static void push(LiveRoom msg,String roomId) {

        if (StringUtils.isNotEmpty(roomId)) {
            try {
                getFromRPCRoomMsgQueueByKey(roomId.hashCode()).offer(msg);
            }catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(roomId+"error"+ex);
            }
            return;
        }
        if (null != msg) {
            getFromRPCRoomMsgQueueByRandom()
                    .offer(msg);
        }
    }


    public static void main(String[] args) {
        getFromRPCRoomMsgQueueByKey("1000".hashCode());
    }

}
