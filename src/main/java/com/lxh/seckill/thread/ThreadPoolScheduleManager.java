package com.lxh.seckill.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * created by lanxinghua@2dfire.com on 2020/7/22
 */
public class ThreadPoolScheduleManager {
    private static final ScheduledExecutorService LAZY = Executors.newScheduledThreadPool(10);

    public ThreadPoolScheduleManager() {
    }

    public static final ScheduledExecutorService getInstance(){
        return LAZY;
    }
}
