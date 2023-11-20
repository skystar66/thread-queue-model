package com.thread.con.queue.compare;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuliang
 * @version 1.0
 * @project thread-queue-model
 * @description
 * @date 2023/10/30 15:37:37
 */
public class CompareTest {
    public static String REDIS_LIMIT_LUA = "-- LUA脚本会以单线程执行,不会有并发问题，一个脚本中的执行过程中如果报错，那么已执行的操作不会回滚\n" +
            "-- KEYS和ARGV是外部传入进来需要操作的redis数据库中的key,下标从1开始\n" +
            "-- 参数结构: KEYS = [限流的key]   ARGV = [最大令牌数, 每秒生成的令牌数, 本次请求的毫秒数]\n" +
            "local info = redis.pcall('HMGET', KEYS[1], 'last_time', 'stored_token_nums')\n" +
            "local last_time = info[1] --最后一次通过限流的时间\n" +
            "local stored_token_nums = tonumber(info[2]) -- 剩余的令牌数量\n" +
            "local max_token = tonumber(ARGV[1])\n" +
            "local token_rate = tonumber(ARGV[2])\n" +
            "local current_time = tonumber(ARGV[3])\n" +
            "local past_time = 0\n" +
            "local rateOfperMills = token_rate/1000 -- 每毫秒生产令牌速率\n" +
            "\n" +
            "if stored_token_nums == nil then\n" +
            "    -- 第一次请求或者键已经过期\n" +
            "    stored_token_nums = max_token --令牌恢复至最大数量\n" +
            "    last_time = current_time --记录请求时间\n" +
            "else\n" +
            "    -- 处于流量中\n" +
            "    past_time = current_time - last_time --经过了多少时间\n" +
            "\n" +
            "    if past_time <= 0 then\n" +
            "        --高并发下每个服务的时间可能不一致\n" +
            "        past_time = 0 -- 强制变成0 此处可能会出现少量误差\n" +
            "    end\n" +
            "    -- 两次请求期间内应该生成多少个token\n" +
            "    local generated_nums = math.floor(past_time * rateOfperMills)  -- 向下取整，多余的认为还没生成完\n" +
            "    stored_token_nums = math.min((stored_token_nums + generated_nums), max_token) -- 合并所有的令牌后不能超过设定的最大令牌数\n" +
            "end\n" +
            "\n" +
            "local returnVal = 0 -- 返回值\n" +
            "\n" +
            "if stored_token_nums > 0 then\n" +
            "    returnVal = 1 -- 通过限流\n" +
            "    stored_token_nums = stored_token_nums - 1 -- 减少令牌\n" +
            "    -- 必须要在获得令牌后才能重新记录时间。举例: 当每隔2ms请求一次时,只要第一次没有获取到token,那么后续会无法生产token,永远只过去了2ms\n" +
            "    last_time = last_time + past_time\n" +
            "end\n" +
            "\n" +
            "-- 更新缓存\n" +
            "redis.call('HMSET', KEYS[1], 'last_time', last_time, 'stored_token_nums', stored_token_nums)\n" +
            "-- 设置超时时间\n" +
            "-- 令牌桶满额的时间(超时时间)(ms) = 空缺的令牌数 * 生成一枚令牌所需要的毫秒数(1 / 每毫秒生产令牌速率)\n" +
            "redis.call('PEXPIRE', KEYS[1], math.ceil((1/rateOfperMills) * (max_token - stored_token_nums)))\n" +
            "\n" +
            "return returnVal";

    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");


        System.out.println(REDIS_LIMIT_LUA);
        System.out.println(list.subList(0,2));

    }

}
