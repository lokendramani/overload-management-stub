package com.overload;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class QCMProcessor {
    private BlockingQueue<Message> rmaOutputQueue;

    private List<BlockingQueue<Message>> qcmSiteList;

    private ExecutorService executorService;

    private static final Logger LOGGER = Logger.getLogger(QCMProcessor.class);

    public QCMProcessor(BlockingQueue<Message> rmaOutputQueue, List<BlockingQueue<Message>> qcmSites){

        this.rmaOutputQueue = rmaOutputQueue;
        this.qcmSiteList = qcmSites;
    }



     public void spawnQCMThread(){
        List<Runnable> runnables = new LinkedList<>();
        for (BlockingQueue<Message> qcmSite: qcmSiteList) {
            Runnable runnable = ()->{
                int i = 0;
                while(qcmSite.size() > 0){
                    try {
                    Message message = qcmSite.take();

                    processMessage(message);

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    i++;
                }
                LOGGER.info("Processed "+ i+" Messages:");
            };
            runnables.add(runnable);
        }
        executorService = Executors.newFixedThreadPool(qcmSiteList.size());
        for(Runnable qcmProcess:runnables){
            executorService.submit(qcmProcess);
        }
    }



    public void stop() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        LOGGER.info("QCMProcessor shutdown triggered:");
        executorService.shutdown();
        long endTime = System.currentTimeMillis();
        LOGGER.info("QCMProcessor shutdown passed:"+(endTime - startTime));
        startTime = System.currentTimeMillis();
        executorService.awaitTermination(10000,TimeUnit.SECONDS);
        LOGGER.info("Waited for Termination:"+( System.currentTimeMillis() - startTime));
    }
    public void processMessage(Message message) throws InterruptedException {

        Thread.sleep(10);
        rmaOutputQueue.offer(message);
    }


}
