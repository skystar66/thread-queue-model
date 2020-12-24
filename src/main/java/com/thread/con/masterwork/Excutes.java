package com.thread.con.masterwork;//package com.liveme.demo.masterwork;
//
//import com.alibaba.fastjson.JSONObject;
//import com.liveme.demo.util.DButil;
//import com.liveme.demo.worker.QueryMongoMsgResultWorker;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//
//import java.util.*;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.FutureTask;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class Excutes {
//
//    private static final Map<Integer, String> MsgIndex_TABLE_MAP = new HashMap<Integer, String>();
//
//    private static final int MsgIndex_TABLE_MaxTableCount = 512;// 每个库最多maxTableCount张表
//    private final static String MsgIndex_TABLE_Collection = "msgindex";
//
//    private final static String Msg_Table_Collection = "chatmsg";
//
//
//    private static int workNum = 5;
//
//    private static final Map<Integer, String> Msg_TABLE_MAP = new HashMap<Integer, String>();
//
//
//    public static void init() {
//        MsgIndex_TABLE_MAP.clear();
//        for (int i = 0; i < MsgIndex_TABLE_MaxTableCount; i++) {
//            MsgIndex_TABLE_MAP.put(i, MsgIndex_TABLE_Collection + i);
//        }
//
//
//        Msg_TABLE_MAP.clear();
//        for (int i = 0; i < MsgIndex_TABLE_MaxTableCount; i++) {
//            Msg_TABLE_MAP.put(i, Msg_Table_Collection + i);
//        }
//
//    }
//
//    public static Integer runTasky() {
//
////        return getMsgThanSequenceMock(1188742971189764096l,1188739124417404929l,"liveme",0l,20,10);
////        queryChatHistoryMsgThanSequenceAndEndTime("909549979881046016", "liveme",
////                0l, 20, 10, getTimestapsByDays(3), 1);
//
//        return queryChatHistoryMsgThanSequenceAndEndTime();
////        return null;
//    }
//
//    public static List getMsgThanSequenceMock(Long from, Long to, String appId, Long sequence, int count, Integer type) {
//        List<Object> result = new CopyOnWriteArrayList<>();
//        long startTime = System.currentTimeMillis();
//        List<DBObject> dbres = getMsgIndexThanSequence(from, to, appId, sequence, count, type, getTimestapsByDays(3), 1);
////        startTime = System.currentTimeMillis();
////        if (dbres != null && !dbres.isEmpty()) {
////            BasicDBObject object = new BasicDBObject();
//////            CountDownLatch countDownLatch = new CountDownLatch(dbres.size());
////            Master master = new Master(new Worker(), 50);
////            dbres.stream().forEach(dbObject -> {
////                long msgIdValue = (long) dbObject.get("msgId");
////                String value = String.valueOf(msgIdValue);
////                object.clear();
////                object.put("msgId", msgIdValue);
////                Task task = new Task();
////                task.setObject(object);
////                task.setTable(getMsgTableName(value));
////                task.setUserId(userId);
////                task.setValue(value);
////                master.submit(task);
////            });
////
////            master.execute();
////            if (master.isComplete()) {
////
////                System.out.println("共 ： "+dbres.size()+"条数据，耗时："+(System.currentTimeMillis()-startTime)+"ms");
////                return master.getResult();
////            }
//
//        return dbres;
//    }
//
//
//    public static List getMsgThanSequenceMock(String userId, String appId, Long sequence, int count, Integer type) {
//        List<Object> result = new CopyOnWriteArrayList<>();
//        long startTime = System.currentTimeMillis();
//        List<DBObject> dbres = getMsgIndexThanSequence(userId, appId, sequence, count, type, getTimestapsByDays(3));
//        startTime = System.currentTimeMillis();
//        if (dbres != null && !dbres.isEmpty()) {
//            BasicDBObject object = new BasicDBObject();
////            CountDownLatch countDownLatch = new CountDownLatch(dbres.size());
//            Master master = new Master(new Worker(), 50);
//            dbres.stream().forEach(dbObject -> {
//                long msgIdValue = (long) dbObject.get("msgId");
//                String value = String.valueOf(msgIdValue);
//                object.clear();
//                object.put("msgId", msgIdValue);
//                Task task = new Task();
//                task.setObject(object);
//                task.setTable(getMsgTableName(value));
//                task.setUserId(userId);
//                task.setValue(value);
//                master.submit(task);
//            });
//
//            master.execute();
//            if (master.isComplete()) {
//
//                System.out.println("共 ： " + dbres.size() + "条数据，耗时：" + (System.currentTimeMillis() - startTime) + "ms");
//                return master.getResult();
//            }
//        }
//
//        return dbres;
//    }
//
//
//    /**
//     * 通过用户Id,sequence,type查找到count的数据
//     *
//     */
//    public static Integer queryChatHistoryMsgThanSequenceAndEndTime() {
//        long startTime = System.currentTimeMillis();
//        /**计算共需要多少个TASK*/
//        int sumTaskNum = 10;
//        System.out.println("共需：" + sumTaskNum + "个线程！");
//        List<FutureTask<Integer>> queryMongoMsgsResFutureTasks = new ArrayList<>(sumTaskNum);
//        for (int i = 1; i <= sumTaskNum; i++) {
//            QueryMongoMsgResultWorker task = new QueryMongoMsgResultWorker(i * 10 + 1, (i + 1) * 10);
//            FutureTask<Integer> futureTask = new FutureTask<>(task);
//            queryMongoMsgsResFutureTasks.add(futureTask);
//
//            Thread worker = new Thread(futureTask, "慢速累加器线程" + i);
//            worker.start();
//        }
//        AtomicInteger total = new AtomicInteger();
//        queryMongoMsgsResFutureTasks.stream().forEach(future -> {
//            try {
//                total.addAndGet(future.get()); // get() 方法会阻塞直到获得结果
//            } catch (Exception ex) {
//            }
//        });
//        System.out.println("获取 ：" + 110 + "条数据，共需要耗时:" + (System.currentTimeMillis() - startTime) + "ms");
//        return total.get();
//    }
//
//
//    /**
//     * 获取N天前的时间
//     */
//    public static long getTimestapsByDays(int days) {
//        Calendar calendar2 = Calendar.getInstance();
//        calendar2.add(Calendar.DATE, -days);
//        return calendar2.getTime().getTime();
//    }
//
//
//    public static List<DBObject> getMsgIndexThanSequence(String userId, String appId, long sequence, int count, Integer type,
//                                                         Long endTime) {
//
//
//        DBObject where = new BasicDBObject();
//        where.put("to", userId);
//        where.put("appId", appId);
//        if (type != null && type > 0) {
//            where.put("msgType", type.intValue());
//        }
//        if (count > 0) {
//            where.put("sequence", new BasicDBObject("$gt", sequence));
//        } else {
//            where.put("sequence", new BasicDBObject("$lt", sequence));
//        }
////        where.put("createDateTime", new BasicDBObject("$gt", endTime));
//
//        DBObject sort = new BasicDBObject("sequence ", 1);
//        List<DBObject> dbres = findPage(String.valueOf(userId), where, sort, getMsgIndexTableName(String.valueOf(userId)), Integer.valueOf(count));
//        return dbres;
//    }
//
//
//    public static List<DBObject> getMsgIndexThanSequence(Long from, Long to, String appId, long sequence, int count, Integer type,
//                                                         Long endTime, int pageNum) {
//
//
//        DBObject where = new BasicDBObject();
//        where.put("to", to.toString());
//        where.put("from", from.toString());
//        where.put("appId", appId);
//        if (type != null && type > 0) {
//            where.put("msgType", type.intValue());
//        }
//        if (count > 0) {
//            where.put("sequence", new BasicDBObject("$gt", sequence));
//        } else {
//            where.put("sequence", new BasicDBObject("$lt", sequence));
//        }
//        where.put("createDateTime", new BasicDBObject("$gt", endTime));
//
//        DBObject sort = new BasicDBObject("sequence ", 1);
//        List<DBObject> dbres = findPage(String.valueOf(to), where, sort, getMsgIndexTableName(String.valueOf(to)), Integer.valueOf(count));
//        return dbres;
//    }
//
//
//    public static List<DBObject> findPage(String dbKey, DBObject where, DBObject sort, String collectionName, int count) {
//        List<DBObject> list = null;
//        try {
//            DBCollection conn = DButil.getMap().get("session").getCollection(collectionName);
//            list = conn.find(where).limit(count).sort(sort).toArray();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return list;
//    }
//
//
//    public static List<DBObject> findPage(String dbKey, DBObject where, DBObject sort, String collectionName) {
//        List<DBObject> list = null;
//        try {
//            DBCollection conn = DButil.getMap().get("session").getCollection(collectionName);
//            list = conn.find(where).sort(sort).toArray();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return list;
//    }
//
//    public static String getMsgIndexTableName(String collectionKey) {
//        int numcode = collectionKey.hashCode();
//        if (numcode == Integer.MIN_VALUE) {
//            numcode = Integer.MAX_VALUE;
//        }
//        int num = Math.abs(numcode);
//        long i = num % MsgIndex_TABLE_MAP.size();
//        // 获取表名
//        return MsgIndex_TABLE_MAP.get((int) i);
//    }
//
//
//    public static String getMsgTableName(String msgId) {
//        int numcode = msgId.hashCode();
//        if (numcode == Integer.MIN_VALUE) {
//            numcode = Integer.MAX_VALUE;
//        }
//        int num = Math.abs(numcode);
//        long i = num % Msg_TABLE_MAP.size();
//        // 获取表名
//        return Msg_TABLE_MAP.get((int) i);
//    }
//
//
//}
