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
    private WebSocketSession blackStonePlayer;
    private WebSocketSession whiteStonePlayer;
    private int[][] blackStones = new int[18][18];
    private int[][] whiteStones = new int[18][18];

    public Match(String id, WebSocketSession blackStonePlayer, WebSocketSession whiteStonePlayer) {
        this.id = id;
        this.blackStonePlayer = blackStonePlayer;
        this.whiteStonePlayer = whiteStonePlayer;
    }

    public boolean isFinish(String color, int row, int col) {
        int[][] stones = "B".equals(color) ? blackStones : whiteStones;
        stones[row][col]++; // 돌 놓기

        // 4가지 방향 검사 (가로, 세로, 대각선 ↘, 대각선 ↙)
        return checkDirection(stones, row, col, 1, 0) ||  // 가로 →
                checkDirection(stones, row, col, 0, 1) ||  // 세로 ↓
                checkDirection(stones, row, col, 1, 1) ||  // 대각선 ↘
                checkDirection(stones, row, col, 1, -1);   // 대각선 ↙
    }

    private boolean checkDirection(int[][] stones, int row, int col, int dRow, int dCol) {
        int count = 1; // 현재 돌 포함
        count += countStones(stones, row, col, dRow, dCol);    // 한쪽 방향 체크
        count += countStones(stones, row, col, -dRow, -dCol);  // 반대 방향 체크
        return count >= 5; // 5개 이상이면 승리
    }

    private int countStones(int[][] stones, int row, int col, int dRow, int dCol) {
        int count = 0;
        int newRow = row + dRow;
        int newCol = col + dCol;

        while (isValid(newRow, newCol) && stones[newRow][newCol] > 0) {
            count++;
            newRow += dRow;
            newCol += dCol;
        }
        return count;
    }

    private boolean isValid(int row, int col) {
        return row >= 0 && row <= 18 && col >= 0 && col <= 18;
    }

}
