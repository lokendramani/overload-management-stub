package com.overload;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class DDRSStub {
    private BlockingQueue<Message> marbenQueue;

    private BlockingQueue<Message> rmaInputQueue;
    private int qcmSites;
    private int numberOfMessages;
    private static final Logger LOGGER = Logger.getLogger(DDRSStub.class);
    public DDRSStub(BlockingQueue<Message> marbenQueue, int numberOfMessages, BlockingQueue<Message> rmaInputQueue, int qcmSites){
        this.marbenQueue = marbenQueue;
        this.rmaInputQueue = rmaInputQueue;
        this.qcmSites = qcmSites;
        this.numberOfMessages = numberOfMessages;
    }

    public void start() throws InterruptedException {
        Message message = marbenQueue.take();
        int count = 0 ;
        while (numberOfMessages > count) {
            try {

                if (message != null) {
                    message = processMessage(message);
                    rmaInputQueue.offer(message);
                    count++;
                }
                message = marbenQueue.poll(100, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private Message processMessage(Message message) throws InterruptedException {
        int destId = getDestinationId();
        message.setDestinationId(destId);
        Thread.sleep(10);
        return message;
    }

    private int getDestinationId() {
        Random random = new Random();
        return random.nextInt(qcmSites);
    }

}
