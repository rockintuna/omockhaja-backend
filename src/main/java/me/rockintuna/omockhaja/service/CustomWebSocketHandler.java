package me.rockintuna.omockhaja.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private final UserContextService userContextService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("id:{} join.", session.getId());
        userContextService.addSession(session);
        userContextService.sendMessageTo("hello", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    }
}
