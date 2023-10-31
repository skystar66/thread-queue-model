package com.thread.con.hash;

import java.util.*;

/**
 * @author xuliang
 * @version 1.0
 * @project bw-server
 * @description
 * @date 2023/9/20 09:31:24
 */
public class HashCircleLoadBalancer {


    private static final long FNV_32_INIT = 2166136261L;
    private static final int FNV_32_PRIME = 16777619;
    // Hash 环
    private static SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();
    // 每台服务器需要设置的虚拟节点数
    private static final int VIRTUAL_NODES = 1000;




    public static void addServerNode(String serverNodeName) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            String VNNode = serverNodeName + "&&VN" + String.valueOf(i);
            int hash = getHashCode(VNNode);
            sortedMap.put(hash, serverNodeName);
        }
    }

    public static  void initServerNode(List<String> serverList) {
        sortedMap.clear();
        for (String server : serverList) {
            addServerNode(server);
        }
    }


    public static String selectServerNode(String requestURL) {
        int hash = getHashCode(requestURL);
        // 向右寻找第一个 key
        SortedMap<Integer, String> subEntry = sortedMap.tailMap(hash);
        // 设置成一个环，如果超过尾部，则取第一个点
        String server = null;
        if (subEntry.isEmpty()) {
            server = sortedMap.get(sortedMap.firstKey());
        } else {
            server = subEntry.get(subEntry.firstKey());
        }
        return server;
    }


    public static int getHashCode(String origin) {

        final int p = FNV_32_PRIME;
        int hash = (int) FNV_32_INIT;
        for (int i = 0; i < origin.length(); i++) {
            hash = (hash ^ origin.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        hash = Math.abs(hash);

        return hash;
    }


    public static void main(String[] args) {
        List<String> serverList = new ArrayList<>();
        // 构造服务器数据
        for (int i = 0; i < 10; i++) {
            String s = new StringBuilder().append("192.168.0.").append(String.valueOf(i)).toString();
            serverList.add(s);
        }
        /**一共10台server 节点*/
        initServerNode(serverList);
        HashMap<String, String> map = new HashMap<>();
        // 模拟一百万请求
        String[] nodes = getIPAddress(100000);
        // <节点，服务器>
        for (int i = 0; i < nodes.length; i++) {
            // 选择服务器
            String serverIP = selectServerNode(nodes[i]);
            // 记录服务器信息
            map.put(nodes[i], serverIP);
        }
//        double v = StatisticsAnalysisUtil.standardDeviation(StatisticsAnalysisUtil.analysis(map));
//        System.out.println("服务物理数量:" + serverList.size() + ",虚拟节点数量:" + VIRTUAL_NODES * serverList.size() + ",一共:" + nodes.length + " 请求数量，平均方差: " + v);
    }

    public static String[] getIPAddress(int num) {
        String[] res = new String[num];
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            res[i] = String.valueOf(random.nextInt(256)) + "." + String.valueOf(random.nextInt(256)) + "."
                    + String.valueOf(random.nextInt(256)) + "." + String.valueOf(random.nextInt(256)) + ":"
                    + String.valueOf(random.nextInt(9999));
        }
        return res;
    }
}
