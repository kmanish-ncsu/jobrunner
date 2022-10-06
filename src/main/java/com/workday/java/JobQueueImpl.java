package com.workday.java;

import java.util.List;

public class JobQueueImpl implements JobQueue {

    public JobQueueImpl(List<Job> initialQueue) {
        QueueManager.init(initialQueue);
    }

    @Override
    public synchronized Job pop() {
        if (QueueManager.queueEmpty()) {
            try {
                Thread.sleep(Long.MAX_VALUE);
                throw new RuntimeException("end of the world");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            return QueueManager.getNextJob();
        }
    }

    @Override
    public int length() {
        return QueueManager.getCustomerQueue().size();
    }
}
