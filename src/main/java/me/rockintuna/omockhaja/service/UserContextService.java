package me.rockintuna.omockhaja.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rockintuna.omockhaja.domain.Match;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;

@Service
@Getter
@Slf4j
public class UserContextService {

    private final Queue<WebSocketSession> sessionQueue = new ArrayDeque<>();
    private final Map<String, WebSocketSession> clients = new HashMap<>();
    private final Map<String, Match> matchMap = new HashMap<>();

    @PostConstruct
    public void setup() {
        Thread thread = new Thread(matching);
        thread.start();
    }

    public void addSession(WebSocketSession session) {
        sessionQueue.add(session);
        clients.put(session.getId(), session);
    }

    public void sendMessageTo(String message, String id) {
        try {
            if (clients.get(id).isOpen()) {
                clients.get(id).sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Runnable matching = () -> {
        while (true) {
            try {
                if (sessionQueue.size() < 2) {
                    sleep(1000L);
                } else {
                    String matchId = UUID.randomUUID().toString();
                    WebSocketSession user1 = sessionQueue.poll();
                    WebSocketSession user2 = sessionQueue.poll();
                    log.info("match:{}", matchId);
                    matchMap.put(matchId, new Match(matchId, user1, user2));
                    sendMessageTo("match:" + "B:" + matchId, user1.getId());
                    sendMessageTo("match:" + "W:" + matchId, user2.getId());
                    sleep(100L);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    };
}
