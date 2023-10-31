package com.thread.con.hash;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuliang
 * @version 1.0
 * @project bw-server
 * @description
 * @date 2023/9/19 20:16:50
 */
public class KafkaPartitionHelper {



    public static void init(int num) {
        List<String> servers = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            servers.add(String.valueOf(i));
        }
        HashCircleLoadBalancer.initServerNode(servers);
    }


    /**
     * @param exchangeEmail
     * @return int
     * @description 获取分区index
     * @author xuliang
     * @date 2023/9/19 20:17:20
     */
    public static int getPartitionIndex(String exchangeEmail) {
        String serverNode = HashCircleLoadBalancer.selectServerNode(exchangeEmail);
        if (!StringUtils.isEmpty(serverNode)) {
            return Integer.parseInt(serverNode);
        }
        return 0;
    }


}
