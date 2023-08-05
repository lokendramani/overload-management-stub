package com.overload;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RMAQueueProcessor {
    private BlockingQueue<Message> rmaInputQueue;
    private BlockingQueue<Message> rmaOutputQueue;

    private BlockingQueue<Message> responseQueue;
    private static final Logger LOGGER = Logger.getLogger(RMAQueueProcessor.class);

    private List<BlockingQueue<Message>> qcmSiteList;

    private int numberOfMessages;
    

    public RMAQueueProcessor(BlockingQueue<Message> rmaInputQueue, BlockingQueue<Message> rmaOutputQueue, BlockingQueue<Message> responseQueue, List<BlockingQueue<Message>> qcmSites, int numberOfMessage){
        this.rmaInputQueue = rmaInputQueue;
        this.rmaOutputQueue = rmaOutputQueue;
        this.responseQueue = responseQueue;
        this.qcmSiteList = qcmSites;
        this.numberOfMessages = numberOfMessage;
        }



    public void sendToQCMSites()throws InterruptedException{
       long startTime = System.currentTimeMillis();
       while(rmaInputQueue.size() > 0){
            Message message = rmaInputQueue.take();
            sendMessageToDestination(message);
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("Message pushed to QCMSites in "+(endTime-startTime)+" milis");

        int sum = 0;
        for (int j = 0; j < qcmSiteList.size() ; j++) {
            LOGGER.info("Site "+ j +": "+qcmSiteList.get(j).size());
            sum += qcmSiteList.get(j).size();
        }



    }

    private void sendMessageToDestination(Message message) {
        int siteId = message.getDestinationId();
        qcmSiteList.get(siteId).offer(message);
     }



    public void sendResponse() throws InterruptedException {
        int count = 0;
        while(numberOfMessages > count){
            Message message =  rmaOutputQueue.take();
            responseQueue.offer(message);
            count++;
        }
    }
}
