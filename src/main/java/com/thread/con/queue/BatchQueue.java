package com.thread.con.queue;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;


/**
 * @author xulaing
 * @desc:多线程批处理
 * @date 2019年06月12日 18:01:55
 */
public class BatchQueue<T> {
    private static int batchSize=100; // 每次处理股票明细条数 2000 条
    private static int timeoutInMs=500;//0.5秒检查一次

    private AtomicBoolean isLoopingStock = new AtomicBoolean(false);//批量操作股票原子操作，使用线程安全,初始化设置从0开始


    private AtomicLong startStock = new AtomicLong(System.currentTimeMillis());//原子操作，使用线程安全，使用当前秒作为预定值


    //批量添加股票代码queue
    public BlockingQueue<T> queueStock = new LinkedBlockingQueue<>();



    /**
     * 添加队列信息
     *
     * @param
     * @return
     */

    public boolean add(T t) {
        boolean result = queueStock.add(t);
        if (!isLoopingStock.get() && result) {
            isLoopingStock.set(true);
            startStockLoop();
        }
        return result;
    }


    /**
     * 批量处理股票队列信息
     *
     * @param
     * @return
     */

    private void startStockLoop() {
        new Thread(() -> {
            startStock = new AtomicLong(System.currentTimeMillis());
            while (true) {
                long last = System.currentTimeMillis() - startStock.get();
                if (queueStock.size() >= batchSize || (!queueStock.isEmpty() && last > timeoutInMs)) {
                    //开始业务逻辑处理
                    drainToConsumeStock();
                } else if (queueStock.isEmpty()) {
                    //如果队列中的元素为空，则isLooping 计数从0开始
                    isLoopingStock.set(false);
                    break;
                }
            }
        }).start();
    }


    /**
     * 批量处理股票信息入库
     *
     * @param
     * @return
     */

    private void drainToConsumeStock() {
        List<T> drained = new ArrayList<>();
        int num = queueStock.drainTo(drained, batchSize);
        System.out.println("批量处理消息数量:{}" + num);

        if (num > 0) {
            //开始进行批处理
            startStock.set(System.currentTimeMillis());
        }
    }


}
