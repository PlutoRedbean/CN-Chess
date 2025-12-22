package Model;

import java.io.Serializable;

// 实现 Serializable 是为了后续通过 Socket 传输对象
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username;
    private int wins;
    private int totalGames;
    private double winRate;

    public User(int id, String username, int wins, int totalGames) {
        this.id = id;
        this.username = username;
        this.wins = wins;
        this.totalGames = totalGames;
        this.winRate = (totalGames == 0) ? 0.0 : (double) wins / totalGames * 100;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public int getWins() { return wins; }
    public int getTotalGames() { return totalGames; }
    public String getWinRateStr() { return String.format("%.1f%%", winRate); }
    
    @Override
    public String toString() {
        return "用户: " + username + " | 胜率: " + getWinRateStr();
    }
}
