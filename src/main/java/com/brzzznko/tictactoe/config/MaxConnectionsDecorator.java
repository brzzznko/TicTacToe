package com.brzzznko.tictactoe.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.concurrent.atomic.AtomicInteger;

public class MaxConnectionsDecorator extends WebSocketHandlerDecorator {

    private final Integer MAX_CONNECTIONS = 1;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public MaxConnectionsDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (activeConnections.incrementAndGet() > MAX_CONNECTIONS) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Max connections reached"));
            return;
        }
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        super.afterConnectionClosed(session, closeStatus);
        activeConnections.decrementAndGet();
    }

}
