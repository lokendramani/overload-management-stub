package com.overload;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger LOGGER = Logger.getLogger(App.class);
    private static final String FILE_NAME = "target/output.txt";

    private static List<BlockingQueue<Message>> qcmSiteList;


    public static void main( String[] args ) throws IOException, InterruptedException {

        int numberOfMessages = 10000;
        int numberOfThreads = 10;
        int qcmSites = 3;

        LinkedBlockingQueue<Message> marbenQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Message> rmaInputQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Message> rmaOutputQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Message> responseQueue = new LinkedBlockingQueue<>();
        initQCMSites(qcmSites);

        WorkloadGenerator generator = new WorkloadGenerator(numberOfMessages,numberOfThreads,marbenQueue);

        DDRSStub ddrsStub = new DDRSStub(marbenQueue,numberOfMessages, rmaInputQueue,qcmSites);
        QCMProcessor qcmProcessor = new QCMProcessor(rmaOutputQueue,qcmSiteList);
        RMAQueueProcessor rmaQueueProcessor = new RMAQueueProcessor(rmaInputQueue,rmaOutputQueue,responseQueue,qcmSiteList,numberOfMessages);

        ResponseReader responseReader = new ResponseReader(responseQueue,FILE_NAME,numberOfMessages);

        generator.start();
        ddrsStub.start();
        rmaQueueProcessor.sendToQCMSites();
        qcmProcessor.spawnQCMThread();
        rmaQueueProcessor.sendResponse();
        responseReader.start();
        qcmProcessor.stop();
        generator.stop();


    }

    private static void initQCMSites(int qcmSites) {
        qcmSiteList = new LinkedList<>();
        for (int i = 0; i < qcmSites; i++) {
            qcmSiteList.add(new LinkedBlockingQueue<>());

        }
    }
}
