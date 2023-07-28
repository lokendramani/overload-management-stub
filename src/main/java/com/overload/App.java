package com.overload;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
        WorkloadGenerator generator = new WorkloadGenerator(90,messageQueue); // messages per second
        ResponseReader responseReader = new ResponseReader(messageQueue);
        generator.start();
        responseReader.start();

        // Sleep for a while to observe the messages being sent (replace this with your application logic)
        try {
            Thread.sleep(10000); // Wait for 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("in main");
        generator.stop();
        System.out.println("load generator stopped");
        responseReader.stop();
        System.out.println("consumer stopped");

    }
}
