package com.thread.con.result;


import java.util.concurrent.atomic.AtomicLong;

public class StaticMessageRecord {

    public static AtomicLong atomicLong = new AtomicLong(0);

////
    static {

//        init();
    }


    public static void init(){


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {


                    try {
                        System.out.println(atomicLong.get()+"条消息");
                        Thread.sleep(2000);
                    }catch (Exception ex) {

                    }


                }
            }
        }).start();


    }

}
