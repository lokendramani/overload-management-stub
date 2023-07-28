package com.overload;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResponseReader {
    private final ExecutorService consumerExecutor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<Message> messageQueue;

    public ResponseReader(BlockingQueue<Message> mQueue){
        messageQueue = mQueue;
    }
    public void start(){
        Runnable consumerTask = () -> {
            while (true) {
                try {

                    Message message = messageQueue.take();
                    //processing delay.
                    Thread.sleep(10);
                    System.out.println("Received message: " + message.getType()+" QueueSize"+messageQueue.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        consumerExecutor.submit(consumerTask);
    }
    public void stop() {
        consumerExecutor.shutdown();
    }
}
