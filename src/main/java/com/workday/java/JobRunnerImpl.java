package com.workday.java;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 * Your code goes here
 *
 */

public class JobRunnerImpl implements JobRunner {

//    ScheduledExecutorService service1 = Executors.newScheduledThreadPool(noOfThreads);

//    CompletableFuture<Job> completableFuture = CompletableFuture.supplyAsync();
    @Override
    public void run(JobQueue jobQueue) {
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

//        service1.isTerminated();
    }

    @Override
    public void shutdown() {

    }
}
