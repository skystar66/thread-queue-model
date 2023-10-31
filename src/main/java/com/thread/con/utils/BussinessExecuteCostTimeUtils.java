package com.thread.con.utils;

import org.apache.commons.lang3.RandomUtils;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/27 15:14:18
 */
public class BussinessExecuteCostTimeUtils {





    public static long costTime(){
        long sleep = RandomUtils.nextLong(1, 4);
        return sleep;
    }


}
