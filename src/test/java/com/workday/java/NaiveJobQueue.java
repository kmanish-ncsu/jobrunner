package com.workday.java;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NaiveJobQueue implements JobQueue {

    public NaiveJobQueue(List<Job> initialQueue) {
        QueueSingleton.init(initialQueue);
    }

    @Override
    public synchronized Job pop() {
        if (QueueSingleton.queueEmpty()) {
            try {
                Thread.sleep(Long.MAX_VALUE);
                throw new RuntimeException("end of the world");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            return QueueSingleton.getNextJob();
        }
    }

    @Override
    public int length() {
        return QueueSingleton.customerQueue.size();
    }
}
