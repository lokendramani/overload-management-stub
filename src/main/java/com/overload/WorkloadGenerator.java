package com.overload;

import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class WorkloadGenerator {
    private final BlockingQueue<Message> messageQueue;
    private final ExecutorService publisherExecutor;

    private final int CORE_POOL_SIZE;

    private final int MAX_POOL_SIZE;

    private final BlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<Runnable>();
    private final int numberOfMessages;
    private final int numberOfThreads;

    private static final Logger LOGGER = Logger.getLogger(WorkloadGenerator.class);

    public WorkloadGenerator(int numberOfMessages, int numberOfThreads, BlockingQueue<Message> mQueue) {
        this.numberOfMessages = numberOfMessages;
        this.numberOfThreads = numberOfThreads;
        messageQueue = mQueue;
        CORE_POOL_SIZE = numberOfThreads;
        MAX_POOL_SIZE = numberOfThreads;
        publisherExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 20,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public void start() {
        int numberOfMessagePerThread = numberOfMessages / numberOfThreads;
        Runnable publisherTask = () -> {
            for (int i = 0; i < numberOfMessagePerThread; i++) {
                String type = getType();
                Message message = new Message(type);
                messageQueue.offer(message);

            }
            LOGGER.info(numberOfMessagePerThread +" Message pushed in "+Thread.currentThread().getName());
        };
        for (int i = 0; i < numberOfThreads; i++) {
            publisherExecutor.execute(publisherTask);
        }
    }

    private String getType() {
        int randomNumber = ThreadLocalRandom.current().nextInt(4);
        switch (randomNumber){
            case 0:
                return "CCRI";
            case 1:
                return "CCRU";
            case 2:
                return "CCRT";
            case 3:
                return "CCRE";
        }
        return null;
    }

    public void stop() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Workload shutdown triggered:");
        publisherExecutor.shutdown();
        long endTime = System.currentTimeMillis();
        LOGGER.info("Workload shutdown passed:"+(endTime - startTime));
        startTime = System.currentTimeMillis();
        publisherExecutor.awaitTermination(1000,TimeUnit.MILLISECONDS);
        LOGGER.info("Waited for Termination:"+( System.currentTimeMillis() - startTime));

    }

}
