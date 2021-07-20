package com.thread.con.rest;

import com.thread.con.disruptor.DisruptorUtil;
import com.thread.con.monitor.ThreadPoolMonitor;
import com.thread.con.queue.MQConsumerMessageEvent;
import com.thread.con.queue.MQProvider;
import com.thread.con.queue.ThreadProviderBatchQueue;
import com.thread.con.vo.Message;
import com.thread.con.vo.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("batch")
public class BatchQueueController {

    private static final Logger logger = LoggerFactory.getLogger(BatchQueueController.class);

    public long time;
    public long time2;

    private static int queueCount = MQProvider.threadCnt;


    private static ExecutorService threadPool = null;
    private static ExecutorService threadPoolExecute = null;


    /**
     * 队列批处理，校验cpu资源
     *
     * @param loopC    循环次数
     * @param msgCount 每个直播间消息数量
     * @desc: 请求样例： http://127.0.0.1:11780/thread/zyQueue2?roomCount=1000&msgCount=1000&loopC=20
     */
    @RequestMapping(value = "queue", method = RequestMethod.GET)
    public String zyQueue2(
            @RequestParam("msgCount") int msgCount,
            @RequestParam("loopC") int loopC) {

        threadPool = ThreadPoolMonitor.
                newFixedThreadPool(loopC, "excute-send-msg");
        for (int i=0;i<loopC;i++) {
            int finalI = i;
            threadPool.submit(()->{
                //每个线程发送msgCount条消息
                for (int j=0;j<msgCount;j++){
                    Message message = new Message(UUID.randomUUID().toString(),j+"",j+ finalI +"");
                    ThreadProviderBatchQueue.getBatchQueue().add(message);
                }


            });
        }
        threadPool.shutdown();
        return "success";
    }




}
