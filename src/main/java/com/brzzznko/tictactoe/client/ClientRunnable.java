package com.brzzznko.tictactoe.client;

import com.brzzznko.tictactoe.model.MoveDTO;
import com.brzzznko.tictactoe.service.GameService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("client")
public class ClientRunnable  {

    private final GameService service;

    @Value("${destination.url}")
    private String serverUrl;

    @Value("${delay.millis}")
    private Integer delay;

    @PostConstruct
    private void init() throws ExecutionException, InterruptedException {
        run();
    }

    public void run() throws ExecutionException, InterruptedException {
        StompSession session = connectToWebSocket();
        subscribeToTopics(session);

        while (!Thread.currentThread().isInterrupted()) {
            service.nextStep(session);
            Thread.sleep(delay);
        }
    }

    private StompSession connectToWebSocket() throws ExecutionException, InterruptedException {
        log.info("Connecting to web socket");

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "ws://" + serverUrl + "/connection";

        return stompClient.connectAsync(url, new StompSessionHandlerAdapter() {}).get();
    }

    private void subscribeToTopics(StompSession session) {
        session.subscribe("/topic/sign", new StompFrameHandler() {
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

        session.subscribe("/topic/accepted/move", new StompFrameHandler() {
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

        session.subscribe("/topic/requested/move", new StompFrameHandler() {
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

        session.subscribe("/topic/error/move", new StompFrameHandler() {
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
