package uk.co.jawadkho.websockets.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;
import uk.co.jawadkho.websockets.demo.service.QueueService;

public class Router {
    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

    public static void main(String[] args) {
        QueuesWebSocket queuesWebsocket = new QueuesWebSocket();
        QueueService queueService = new QueueService();
        PeriodicUpdater periodicUpdater = new PeriodicUpdater(queueService, queuesWebsocket);

        queuesWebsocket.setListener(periodicUpdater);

        spark.Service s = Service.ignite();
        s.webSocket("/queues", queuesWebsocket);

        s.init();
        LOGGER.info("Started.");
    }
}
