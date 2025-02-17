package me.rockintuna.omockhaja.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Match {
    private String id;
    private WebSocketSession user1;
    private WebSocketSession user2;
}
