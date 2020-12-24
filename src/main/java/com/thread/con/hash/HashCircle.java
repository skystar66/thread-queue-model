//package com.thread.con;
//
//
//import com.liveme.demo.util.MD5;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Created by wenlong on 2020/12/14 6:22 下午
// */
//public class HashCircle {
//
//    private static final int numberOfReplicas = 64;
//
//    private static final int circleNodes = 10240;
//
//    /**
//     * 用于存储不同类型的集群对应的哈希环 例如 : /chat/cluster_tob -> circle    /room/cluster_tob -> circle
//     */
//    private Map<String,String[]> maps = new ConcurrentHashMap<>(5);
//
//    private static volatile com.liveme.demo.msgqueue.HashCircle instance = null;
//
//    private HashCircle(){
//
//    }
//
//    public static com.liveme.demo.msgqueue.HashCircle getInstance(){
//        if(instance == null){
//            synchronized (com.liveme.demo.msgqueue.HashCircle.class){
//                if(instance == null){
//                    instance = new com.liveme.demo.msgqueue.HashCircle();
//                }
//            }
//        }
//        return instance;
//    }
//
//    /**
//     * 集群节点变动需要调用此方法重置哈希环
//     * @param clusterPath 集群zk路径
//     * @param serverList 集群机器列表 使用,分割
//     */
//    public synchronized void init(String clusterPath,String serverList){
//
//        if(StringUtils.isEmpty(serverList)){
//            return;
//        }
//
//        String[] circle = new String[circleNodes];
//
//        String[] servers = serverList.split(",");
//
//        ArrayList<String> totalNodes = new ArrayList<>();
//
//        for (String server:servers) {
//            if(StringUtils.isEmpty(server)){
//                continue;
//            }
//            /**
//             * 每个服务节点生成 numberOfReplicas 个虚拟节点
//             */
//            List<String> virtualNodes = generateVirtualNodes(server);
//            totalNodes.addAll(virtualNodes);
//        }
//
//        hashNodesToCircle(totalNodes,circle);
//
//        // 更新之前的哈希环
//        maps.put(clusterPath,circle);
//    }
//
//
//    /**
//     * 根据clusterPath 和 key 获取映射后的结点
//     * @param clusterPath 集群zk路径
//     * @param key hashkey
//     * @return
//     */
//    public String get(String clusterPath,String key){
//        String[] circle = maps.get(clusterPath);
//        if(circle == null || circle.length == 0){
//            return null;
//        }
//
//        String crypt = MD5.crypt(key);
//        int index = Math.abs(crypt.hashCode()) % circleNodes;
//
//        for(int i=index ;i<circle.length ;i++){
//            String selectNode = circle[i];
//            if(selectNode != null){
//                return selectNode.split("#")[0];
//            }
//        }
//        for(int j=0;j<index ;j++){
//            String selectNode = circle[j];
//            if(selectNode != null){
//                return selectNode.split("#")[0];
//            }
//        }
//        return null;
//    }
//
//
//    private static void hashNodesToCircle(ArrayList<String> totalNodes, String[] circle) {
//
//        for (String node:totalNodes) {
//            String crypt = MD5.crypt(node);
//            int abs = Math.abs(crypt.hashCode());
//            int index =(int) (abs % circleNodes);
//            circle[index] = node;
//        }
//    }
//
//    private static List<String> generateVirtualNodes(String server) {
//        ArrayList<String> objects = new ArrayList<>(numberOfReplicas);
//        for(int i=0;i<numberOfReplicas;i++){
//            objects.add(server + "#" + i);
//        }
//        return objects;
//    }
//
//
//    /*public static void main(String[] args) {
//
//
//        HashCircle instance = HashCircle.getInstance();
//
//        instance.init("/chat/cluster","127.0.0.1,127.0.0.2,127.0.0.3,127.0.0.4");
//
//
//        HashMap<String, String> map = new HashMap<>();
//
//        for(int i=0;i<50000;i++){
//            String node = "test_" + i + "node";
//            String result = instance.get("/chat/cluster", node);
//            map.put(node,result);
//        }
//
//        Map<String, List<String>> collect = map.values().stream().collect(Collectors.groupingBy(String::intern));
//
//        // 移除节点
//        instance.init("/chat/cluster","127.0.0.2,127.0.0.3,127.0.0.4");
//
//        int num = 0;
//        HashMap<String, String> map3 = new HashMap<>();
//        for(int i=0;i<50000;i++){
//            String node = "test_" + i + "node";
//            String result = instance.get("/chat/cluster", node);
//            map3.put(node,result);
//            if(!map.get(node).equals(result)){
//                num ++;
//            }
//        }
//
//        Map<String, List<String>> collect3 = map3.values().stream().collect(Collectors.groupingBy(String::intern));
//        System.out.println(num);
//
//        // 添加节点
//        instance.init("/chat/cluster","127.0.0.1,127.0.0.2,127.0.0.3,127.0.0.4,127.0.0.5");
//
//        num = 0;
//        HashMap<String, String> map5 = new HashMap<>();
//        for(int i=0;i<50000;i++){
//            String node = "test_" + i + "node";
//            String result = instance.get("/chat/cluster", node);
//            map5.put(node,result);
//            if(!map.get(node).equals(result)){
//                num ++;
//            }
//        }
//        Map<String, List<String>> collect5 = map5.values().stream().collect(Collectors.groupingBy(String::intern));
//        System.out.println(num);
//
//
//    }*/
//
//
//
//}
