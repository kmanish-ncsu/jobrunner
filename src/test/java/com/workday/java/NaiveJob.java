package com.workday.java;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class NaiveJob implements Job, Runnable {

    private Random random = new Random();

    private long customerId;
    private long uniqueId = random.nextLong();
    private int duration;
    private boolean executed = false;

//    ExecutorService executorService;
//    CompletableFuture<Void> completableFuture;

    public NaiveJob() {
        customerId = random.nextLong();
        duration = 100;
    }

    public NaiveJob(Job job){
        this.customerId = job.customerId();
        this.duration = job.duration();
//        this.executorService = executorService;
    }

    public NaiveJob(long customerId, int duration) {
        this.customerId = customerId;
        this.duration = duration;
    }
    @Override
    public long customerId() {
        return customerId;
    }

    @Override
    public long uniqueId() {
        return uniqueId;
    }

    @Override
    public int duration() {
        return duration;
    }

    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void execute() {
        System.out.println("execute started "+this.customerId+" "+this.uniqueId);
        try {
            Thread.sleep(duration);
            executed = true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("execute finished "+this.customerId+" "+this.uniqueId);
        runNextJob();
    }

    @Override
    public void run() {
        this.execute();
    }

    private synchronized void runNextJob(){
        System.out.println("runNextJob "+this.customerId+" "+this.uniqueId);
        NaiveJob nextJob = (NaiveJob) QueueSingleton.getNextJob();
        System.out.println("runNextJob nextJob "+nextJob.customerId+" "+nextJob.uniqueId);
        if(nextJob!= null) {
            QueueSingleton.submitJob(nextJob);
        }
    }

}
