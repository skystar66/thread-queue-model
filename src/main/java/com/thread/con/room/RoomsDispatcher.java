package com.thread.con.room;


import com.thread.con.monitor.ScheduleThreadPoolMonitor;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenlong on 2020/12/16 3:08 下午
 */
public class RoomsDispatcher {


    public Map<String, LiveRoom> rooms = new ConcurrentHashMap<>();


    private ScheduledExecutorService scheduledExecutorService
            = null;
    private static class SingletonHolder {
        private static final RoomsDispatcher INSTANCE = new RoomsDispatcher();
    }

    public static final RoomsDispatcher getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init()
    {
        startScheduleMessages();
    }

    private void startScheduleMessages() {


        scheduledExecutorService
                = ScheduleThreadPoolMonitor.newSchedule("schedule");
        //每200ms 遍历一次队列
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                RoomsDispatcher.getInstance().batchSend();
            }
        },0,1,TimeUnit.MILLISECONDS);


    }

    public void batchSend() {
        // 获取缓存中所有的直播间消息队列
        Collection<LiveRoom> values = rooms.values();
        for (LiveRoom room:values) {
            room.batchSend2();
        }
    }


    public boolean add(String roomid, String message) {

        LiveRoom liveRoom = getRoom(roomid);
        boolean isFull = liveRoom.add(message);
        return isFull;
    }



    public boolean add2(LiveRoom liveRoom, String message) {

        boolean isFull = liveRoom.add(message);
        return isFull;
    }


    private LiveRoom getRoom(String roomid) {
        LiveRoom liveRoom = rooms.get(roomid);
        return liveRoom;
    }

    public LiveRoom createRoom(String roomId){
        LiveRoom currentRoom = rooms.computeIfAbsent(roomId, key -> {

            LiveRoom newRoom = new LiveRoom();
            newRoom.setRoomId(roomId);

            return newRoom;
        });
        return currentRoom;

    }

    public void cleanRoom(){

        for (Map.Entry<String, LiveRoom> stringLiveRoomEntry : rooms.entrySet()) {
            rooms.remove(stringLiveRoomEntry.getKey());
        }

    }

    public boolean isTermed(){
        while(true){
            if(scheduledExecutorService.isTerminated()){
                System.out.println("所有的子线程都结束了！");
                break;
            }

        }
        return true;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }


    public Map<String, LiveRoom> getRooms() {
        return rooms;
    }

    public void setRooms(Map<String, LiveRoom> rooms) {
        this.rooms = rooms;
    }
}
