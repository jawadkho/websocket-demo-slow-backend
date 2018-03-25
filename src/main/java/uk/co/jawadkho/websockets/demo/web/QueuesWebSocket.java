package uk.co.jawadkho.websockets.demo.web;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jawadkho.websockets.demo.service.Queue;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@WebSocket
public class QueuesWebSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueuesWebSocket.class);

    private final Set<Session> openSessions = ConcurrentHashMap.newKeySet();
    private QueueWebSocketListener listener;

    public void setListener(QueueWebSocketListener listener) {
        LOGGER.info("Listener set.");
        this.listener = listener;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        LOGGER.info("Session {} started.", session.getRemoteAddress().getHostString());
        openSessions.add(session);
        if (listener != null) {
            LOGGER.info("onSessionStarted() with session {}.", session.getRemoteAddress().getHostString());
            listener.onSessionStarted(new QueuesUpdateReceiver(session));
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        openSessions.remove(session);
    }

    public Set<QueuesUpdateReceiver> getOpenSessions() {
        return openSessions.stream()
                .map(QueuesUpdateReceiver::new)
                .collect(Collectors.toSet());
    }

    public class QueuesUpdateReceiver {
        private final Logger LOGGER = LoggerFactory.getLogger(QueuesUpdateReceiver.class);

        private final Gson gson;
        private final Session session;

        QueuesUpdateReceiver(Session session) {
            this.session = session;
            this.gson = new Gson();
        }

        public void send(Queue update) {
            if (session.isOpen()) {
                try {
                    LOGGER.debug("Sending to {} update for Queue: {}", session.getRemoteAddress().getHostString(), update);
                    String serialised = gson.toJson(update);
                    this.session.getRemote().sendString(serialised);
                } catch (IOException e) {
                    LOGGER.error("Session error, closing and discarding session: {}.", e.getMessage());
                    this.session.close();
                    QueuesWebSocket.this.openSessions.remove(this.session);
                }
            } else {
                LOGGER.debug("Session closed, skipping update.");
            }
        }
    }

    public interface QueueWebSocketListener {
        void onSessionStarted(QueuesUpdateReceiver session);
    }
}
