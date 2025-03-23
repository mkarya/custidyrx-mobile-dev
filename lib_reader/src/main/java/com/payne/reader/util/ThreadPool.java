package com.payne.reader.util;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Thread pool tool class, used for asynchronous operations, etc.
 *
 * @author naz
 * Date 2020/7/17
 */
public class ThreadPool {
    private static ThreadPoolExecutor sTpe;

    private ThreadPool() {
    }

    static {
        RejectedExecutionHandler handler = (runnable, threadPoolExecutor) -> {
            LLLog.w("RejectedExecution->" + runnable.getClass());
        };
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(99);
        ThreadFactory factory = Executors.defaultThreadFactory();
        sTpe = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS, workQueue, factory, handler);
    }

    public static ThreadPoolExecutor get() {
        return sTpe;
    }

    public static void execute(Runnable command) {
        if (command != null) {
            sTpe.execute(command);
        }
    }
}
