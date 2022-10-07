package com.workday.java;

import java.util.Random;

public class JobImpl implements Job, Runnable {

    private Random random = new Random();

    private long customerId;
    private long uniqueId = random.nextLong();
    private int duration;
    private boolean executed = false;

    public JobImpl() {
        customerId = random.nextLong();
        duration = 100;
    }

    public JobImpl(Job job){
        this.customerId = job.customerId();
        this.duration = job.duration();
    }

    public JobImpl(long customerId, int duration) {
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
        System.out.println("execute started: customerId "+this.customerId+" uniqueId "+this.uniqueId);
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
        System.out.println("run started "+this.customerId+" "+this.uniqueId);
        this.execute();
    }

    private void runNextJob(){
        JobImpl nextJob = (JobImpl) QueueManager.getNextJob();
        System.out.println("runNextJob nextJob "+nextJob.customerId+" "+nextJob.uniqueId);
        if(nextJob!= null) {
            QueueManager.submitJob(nextJob);
        }
    }

}
