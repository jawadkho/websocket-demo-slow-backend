package uk.co.jawadkho.websockets.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jawadkho.websockets.demo.service.Queue;
import uk.co.jawadkho.websockets.demo.service.QueueService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class PeriodicUpdater implements QueuesWebSocket.QueueWebSocketListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicUpdater.class);

    private final ScheduledExecutorService periodicService = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private final QueueService queueService;
    private final QueuesWebSocket queuesWebsocket;

    public PeriodicUpdater(QueueService queueService, QueuesWebSocket queuesWebsocket) {
        this.queueService = queueService;
        this.queuesWebsocket = queuesWebsocket;
        this.setup();
    }

    private void setup() {
        periodicService.scheduleWithFixedDelay(
                update((update) -> queuesWebsocket.getOpenSessions().forEach(s -> s.send(update))),
                0, 5, TimeUnit.SECONDS);
    }

    private Runnable update(Consumer<Queue> updateConsumer) {
        return () -> {
                if (queuesWebsocket.getOpenSessions().size() > 0) {
                    LOGGER.info("Updating open sessions.");
                    queueService.listEndpoints().forEach((endpoint ->
                            supplyAsync(() -> {
                                QueueService.Attributes attributes = queueService.getAttributes(endpoint);
                                return new Queue(endpoint, attributes);
                            }, executor)
                            .thenAccept(updateConsumer)));
                } else {
                    LOGGER.info("No sessions open. Skipping update.");
                }
        };
    }

    @Override
    public void onSessionStarted(QueuesWebSocket.QueuesUpdateReceiver updateReceiver) {
        update(updateReceiver::send).run();
    }
}
