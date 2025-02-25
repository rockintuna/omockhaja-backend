package me.rockintuna.omockhaja.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rockintuna.omockhaja.domain.Match;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.info("id:{} left the session.", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String[] messages = message.getPayload().split(":");
        String matchId = messages[0];
        String color = messages[1];
        int row = Integer.parseInt(messages[2]);
        int col = Integer.parseInt(messages[3]);
        Match match = userContextService.getMatchMap().get(matchId);
        boolean finish = match.isFinish(color, row, col);

        if ( finish ) {
            if ( color.equals("black") ) {
                userContextService.sendMessageTo("win", match.getBlackStonePlayer().getId());
                userContextService.sendMessageTo("loose", match.getWhiteStonePlayer().getId());
            } else {
                userContextService.sendMessageTo("win", match.getBlackStonePlayer().getId());
                userContextService.sendMessageTo("loose", match.getWhiteStonePlayer().getId());
            }
            userContextService.getMatchMap().remove(matchId);
            for (WebSocketSession player : match.getPlayers()) {
                player.close(CloseStatus.NO_STATUS_CODE);
            }
        } else {
            if ( color.equals("B") ) {
                userContextService.sendMessageTo("update:" + message.getPayload(), match.getWhiteStonePlayer().getId());
            } else {
                userContextService.sendMessageTo("update:" + message.getPayload(), match.getBlackStonePlayer().getId());
            }
        }
    }
}
