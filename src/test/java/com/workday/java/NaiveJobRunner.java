package com.workday.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaiveJobRunner implements JobRunner {


    Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile boolean shutdownFinished = false;

    @Override
    public void run(JobQueue jobQueue) {
        //init ConcurrentHashMap customerJobs MAP using jobQueue, empty the jobQueue, later in separate thread we keep checking jobQueue.length()
        //fetch 1st 12(POOL_SIZE) jobs from MAP & submit
        //whenever a job ends, its task is to fetch next job using getNextJob() & submit it
            // it is getNextJob()'s resp to
                // return jobs which is FAIR to all customers, aka round-robin customers
                // delete returned JOB from MAP
                // getNextJobFromMap() needs to be synchronised & singleton
        //keep doing this till MAP not empty for TIMEOUT period (map empty for timeout duration, run ends) --NO
        //keep doing this till jobQueue.length() NOT >0 for TIMEOUT period
        // keep refreshing customerQueue & customerJobs in separate thread based on jobQueue.length()

//        while (shouldContinue) {
            for(int i=0;i<Runtime.getRuntime().availableProcessors();i++){
                NaiveJob poppedNaiveJob = (NaiveJob) jobQueue.pop();
                if(poppedNaiveJob != null){
                    QueueManager.submitJob(poppedNaiveJob);
                }
            }
//        }

//        shutdownFinished = true;
    }



    @Override
    public void shutdown() {
//        shouldContinue = false;
        System.out.println("1 QueueManager.queueEmpty() "+ QueueManager.queueEmpty()+" "+ QueueManager.getCustomerQueue().size());
        QueueManager.initiateShutdown();
        System.out.println("2 QueueManager.queueEmpty() "+ QueueManager.queueEmpty()+" "+ QueueManager.getCustomerQueue().size());
        while (!QueueManager.queueEmpty()) {
            System.out.println("3 QueueManager.queueEmpty() "+ QueueManager.queueEmpty()+" "+ QueueManager.getCustomerQueue().size());
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
