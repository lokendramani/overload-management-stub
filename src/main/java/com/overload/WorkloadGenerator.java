package com.overload;

import java.util.concurrent.*;

public class WorkloadGenerator {
    private final BlockingQueue<Message> messageQueue;
    private final ScheduledExecutorService publisherExecutor = Executors.newScheduledThreadPool(1);

    private final int messagesPerSecond;

    public WorkloadGenerator(int messagesPerSecond, BlockingQueue<Message> mQueue) {
        this.messagesPerSecond = messagesPerSecond;
        messageQueue = mQueue;
    }

    public void start() {
        Runnable publisherTask = () -> {
            for (int i = 0; i < messagesPerSecond; i++) {
                String type = getType();
                Message message = new Message(type);
                boolean queueFull = messageQueue.offer(message);
                if(queueFull){
                    System.out.println("Queue is full...");
                }
            }
            System.out.println(messagesPerSecond +" Message pushed ");
        };


        publisherExecutor.scheduleAtFixedRate(publisherTask, 0,1 , TimeUnit.SECONDS);


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

    public void stop() {
        publisherExecutor.shutdown();

    }

}
