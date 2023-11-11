package com.qing.fan.common.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ParallelFetcher {
    private final long timeout;
    public final CountDownLatch latch;
    final static ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 200, 1,
            TimeUnit.HOURS, new ArrayBlockingQueue<>(100));

    public ParallelFetcher(int jobSize, long timeoutMill) {
        latch = new CountDownLatch(jobSize);
        timeout = timeoutMill;
    }

    public void submitJob(Runnable runnable) {
        executor.execute(() -> {
            runnable.run();
            latch.countDown();
        });
    }

    public void await() {
        try {
            this.latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }

    public void dispose() {
        executor.shutdown();
    }

}
