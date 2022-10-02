//package com.workday.java.backup;
//
//import com.workday.java.Job;
//import com.workday.java.JobQueue;
//import com.workday.java.JobRunner;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class NaiveJobRunner implements JobRunner {
//
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private volatile boolean shouldContinue = true;
//    private volatile boolean shutdownFinished = false;
//
//    @Override
//    public void run(JobQueue jobQueue) {
//        while (shouldContinue) {
//            Job nextJob = jobQueue.pop();
//            System.out.println(nextJob.uniqueId());
//            nextJob.execute();
//        }
//        logger.info("shutting down");
//        shutdownFinished = true;
//    }
//
//    @Override
//    public void shutdown() {
//        shouldContinue = false;
//        while (!shutdownFinished) {
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//}
