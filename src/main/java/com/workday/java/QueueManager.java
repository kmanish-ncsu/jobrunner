package com.workday.java;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class QueueManager {

    private static ConcurrentLinkedQueue<Long> customerQueue;
    private static Map<Long, ConcurrentLinkedQueue<Job>> customerJobsMap;

    private static volatile boolean shouldContinue = true;

//    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static ExecutorService executorService = Executors.newFixedThreadPool(2);

    static
    {
        customerQueue = new ConcurrentLinkedQueue<Long>();//ArrayBlockingQueue LinkedBlockingQueue ConcurrentLinkedQueue
        customerJobsMap = new ConcurrentHashMap<>();
    }

    public static synchronized void submitJob(Job job){
        System.out.println("Job to run "+job.uniqueId());
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync((JobImpl)job,executorService);
        System.out.println("Job submitted "+job.uniqueId());
    }

    public static synchronized boolean queueEmpty(){
       return customerQueue.isEmpty();
    }

    public static void init(List<Job> initialQueue) {
        addCustomersToQueue(initialQueue);
        initCustomerJobs(initialQueue);
        System.out.println("init done");
    }

    public static void addJobs(List<Job> jobs) {
        shouldContinue = false;
        addCustomersToQueue(jobs);
        addJobsToMap(jobs);
        shouldContinue = true;
    }

    private static void addJobsToMap(List<Job> jobs) {
        Supplier<ConcurrentLinkedQueue<Job>> supplier = () -> new ConcurrentLinkedQueue<Job>();
        Map<Long, ConcurrentLinkedQueue<Job>> collect = jobs.stream().collect(Collectors.groupingBy(Job::customerId, Collectors.toCollection(supplier)));
        collect.forEach((customerId, jobs1) -> {
            ConcurrentLinkedQueue<Job> customerJobQueue = customerJobsMap.get(customerId);
            if(customerJobQueue != null){
                customerJobQueue.addAll(jobs1);
            }else{
                customerJobsMap.put(customerId, jobs1);
            }
        });
    }


    static private void addCustomersToQueue(List<Job> initialQueue) {
        Set<Long> set = new HashSet<>();
        List<Job> jobs = initialQueue.stream().filter(job -> set.add(job.customerId())).collect(Collectors.toList());
        List<Long> uniqueCustomers = jobs.stream().map(job -> job.customerId()).collect(Collectors.toList());
        customerQueue.addAll(uniqueCustomers);
    }

    static private void initCustomerJobs(List<Job> initialQueue) {
        Supplier<ConcurrentLinkedQueue<Job>> supplier = () -> new ConcurrentLinkedQueue<Job>();
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
        ConcurrentLinkedQueue<Job> jobs = customerJobsMap.get(nextCustomer);
        if(jobs==null) System.out.println("jobs null for customer "+nextCustomer);
        if(jobs != null && shouldContinue){
            System.out.println("before poll getNextJob jobqueue size "+jobs.size());
            job = jobs.poll();
            System.out.println("after poll getNextJob jobqueue size "+jobs.size());
            if(!jobs.isEmpty()) {
                customerQueue.add(nextCustomer);
            }else {
                customerJobsMap.remove(nextCustomer);
            }
        }
        System.out.println("customerQueue size"+customerQueue.size());
        System.out.println("customerJobsMap size"+customerJobsMap.size());
        return job;
    }

    public static ConcurrentLinkedQueue<Long> getCustomerQueue() {
        return customerQueue;
    }
}
