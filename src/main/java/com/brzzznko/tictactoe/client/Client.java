package com.brzzznko.tictactoe.client;

import com.brzzznko.tictactoe.model.MoveDTO;
import com.brzzznko.tictactoe.service.GameService;
import com.brzzznko.tictactoe.utility.WebSocketApiConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("client")
public class Client {

    private final GameService service;

    private StompSession session;

    @Value("${destination.url}")
    private String serverUrl;

    @Scheduled(fixedRateString = "${delay.millis}")
    private void run() throws ExecutionException, InterruptedException {
        service.nextStep(getSession());
    }

    private StompSession getSession() throws ExecutionException, InterruptedException {
        if (session != null && session.isConnected()) {
            return session;
        }

        return connectToWebSocket();
    }

    private StompSession connectToWebSocket() throws ExecutionException, InterruptedException {
        log.info("Trying to connect to web socket...");

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "ws://" + serverUrl + WebSocketApiConstants.CONNECTION_PREFIX;
        session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {}).get();
        subscribeToTopics(session);

        log.info("Connected to web socket");

        return session;
    }

    private void subscribeToTopics(StompSession session) {
        session.subscribe(WebSocketApiConstants.DESTINATION_GET_SIGN, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Character.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Character sign = (Character) payload;
                service.setPlayerSign(sign);
            }
        });

        session.subscribe(WebSocketApiConstants.DESTINATION_MOVE_ACCEPTED, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MoveDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                MoveDTO move = (MoveDTO) payload;
                service.acceptMove(move);
            }
        });

        session.subscribe(WebSocketApiConstants.DESTINATION_MOVE_REQUESTED, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MoveDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                MoveDTO move = (MoveDTO) payload;
                service.acceptRequestedMove(session, move);
            }
        });

        session.subscribe(WebSocketApiConstants.DESTINATION_MOVE_REJECTED, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MoveDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                MoveDTO move = (MoveDTO) payload;
                service.rejectMove(move);
            }
        });
    }
}
