package com.workday.java;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * These are integration tests that cover the basic requirements of a JobRunner,
 * the NaiveJobRunner does not meet all of them so some of these tests are ignored.
 * Your implementation of JobRunner should pass this tests, feel free to copy
 * this class and adapt it to your solution.
 */
public class JobRunnerTests {

    @Before
    public void setup() throws InterruptedException {
        Thread.sleep(5000);
    }

    @Test
    public void shouldEventuallyExecuteAllJobs() throws InterruptedException {
        List<Job> jobs = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            jobs.add(new JobImpl());
        }
        // There are 5 jobs of 100ms each = 500ms of cpu time
        JobQueue testQueue = new JobQueueImpl(jobs);
        JobRunner jobRunner = new JobRunnerImpl();
        new Thread(() -> jobRunner.run(testQueue)).start();
        Thread.sleep(1000); // 1s is enough to execute all jobs even for the naive implementation
        assertEquals(testQueue.length(), 0);
        for (Job job : jobs) {
            JobImpl jobImpl = (JobImpl) job;
            assertTrue(jobImpl.isExecuted());
        }
    }

    @Test
    // Fails in the naive implementation
    public void shouldExecuteJobsWithPerformance() throws InterruptedException {
        List<Job> jobs = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            jobs.add(new JobImpl());
        }
        // There are 20 jobs of 100ms each = 2s of cpu time
        JobQueue testQueue = new JobQueueImpl(jobs);
        JobRunner jobRunner = new JobRunnerImpl();
        new Thread(() -> jobRunner.run(testQueue)).start();
        Thread.sleep(1100); // Only 1s wait should be enough for all jobs to be executed
        for (Job job : jobs) {
            JobImpl jobImpl = (JobImpl) job;
            assertTrue(jobImpl.isExecuted());
        }
    }

    @Test
    // Fails in the naive implementation
    public void shouldExecuteJobsWithFairness() throws InterruptedException {
        System.out.println("start");
        List<Integer> customerIds = new ArrayList<>();
        for(int i = 1; i <= 100; i++) {
            customerIds.add(i);
        }
        List<Job> jobs = new ArrayList<>();
        for(Integer customerId: customerIds) {
            for(int i = 1; i <= 100; i++) {
                jobs.add(new JobImpl(customerId, 100));
            }
        }
        // There are 100000 jobs of 100ms each
        JobQueue testQueue = new JobQueueImpl(jobs);
        JobRunner jobRunner = new JobRunnerImpl();
        new Thread(() -> jobRunner.run(testQueue)).start();
        Thread.sleep(10000); // This should be enough to execute about 10% of the jobs on a modern pc
        for(Integer customerId : customerIds) {
            int executedJobs = 0;
            for(Job job: jobs) {
                if(((JobImpl) job).isExecuted() && job.customerId() == customerId.intValue()) {
                    executedJobs++;
                }
            }
            // For every customer there should be at least 1 executed job
            assertTrue(executedJobs > 0);
        }
        System.out.println("tests passed");
//        while (testQueue.length()>0){
//            Thread.sleep(1);
//            System.out.println("LEN !!!!!!!! "+testQueue.length()+" "+Runtime.getRuntime().availableProcessors());
//        }
    }

    @Test
    @Ignore
    public void shouldShutdownGracefully() throws InterruptedException {
        List<Job> jobs = Arrays.asList(new JobImpl(), new JobImpl(), new JobImpl(), new JobImpl());
        JobQueue testQueue = new JobQueueImpl(jobs);
        JobRunner jobRunner = new JobRunnerImpl();
        Thread runningThread = new Thread(() -> jobRunner.run(testQueue));
        runningThread.start();
        System.out.println("5 QueueManager.queueEmpty() "+ QueueManager.queueEmpty()+" "+ QueueManager.getCustomerQueue().size());
        jobRunner.shutdown();
        System.out.println("6 QueueManager.queueEmpty() "+ QueueManager.queueEmpty()+" "+ QueueManager.getCustomerQueue().size());
        assertTrue(testQueue.length() > 0);
    }

}
