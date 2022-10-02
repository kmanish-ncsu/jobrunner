package com.workday.java;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class QueueSingleton {

    private static final QueueSingleton instance;
    static ConcurrentLinkedDeque<Long> customerQueue;
    static Map<Long, ConcurrentLinkedDeque<Job>> customerJobsMap;

    private static volatile boolean shouldContinue = true;

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    static
    {
        instance = new QueueSingleton();
        customerQueue = new ConcurrentLinkedDeque<Long>();//ArrayBlockingQueue LinkedBlockingQueue ConcurrentLinkedQueue
        customerJobsMap = new ConcurrentHashMap<>();
    }

    private QueueSingleton() {
    }


    public static synchronized void submitJob(Job job){
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync((NaiveJob)job,executorService);
    }

    public static QueueSingleton getInstance(){
        return instance;
    }

    public static boolean queueEmpty(){
       return customerQueue.isEmpty();
    }

    public static void init(List<Job> initialQueue) {
        initCustomerQueue(initialQueue);
        initCustomerJobs(initialQueue);
    }

    static private void initCustomerQueue(List<Job> initialQueue) {
        Set<Long> set = new HashSet<>();
        List<Job> jobs = initialQueue.stream().filter(job -> set.add(job.customerId())).collect(Collectors.toList());
        List<Long> uniqueCustomers = jobs.stream().map(job -> job.customerId()).collect(Collectors.toList());
        customerQueue.addAll(uniqueCustomers);
    }

    static private void initCustomerJobs(List<Job> initialQueue) {
        Supplier<ConcurrentLinkedDeque<Job>> supplier = () -> new ConcurrentLinkedDeque<Job>();
        customerJobsMap = initialQueue.stream().collect(Collectors.groupingBy(Job::customerId, Collectors.toCollection(supplier)));
    }

    public static void initiateShutdown(){
        shouldContinue=false;
    }
    public static synchronized Job getNextJob(){
        //fetch next customerid from customerQueue round-robin
        //TODO check if queue empty ?
        Job job = null;
        Long nextCustomer = customerQueue.poll();
        //fetch next job from customerJobs (delete JOB once fetched)
        ConcurrentLinkedDeque<Job> jobs = customerJobsMap.get(nextCustomer);
        if(jobs==null) System.out.println("jobs null for customer "+nextCustomer);
        if(jobs != null && shouldContinue){
            job = jobs.poll();
            if(!jobs.isEmpty()) {
                customerQueue.add(nextCustomer);
            }
        }
        return job;
    }
}
