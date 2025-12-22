package Server;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    // 在服务端运行，故使用localhost
    private static final String URL = "jdbc:mysql://localhost:3306/chess_game?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "chess_game";       // 数据库用户名
    private static final String DB_PASS = "D7yYNNDrZHmi28zz"; // 数据库密码

    // 加载驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASS);
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getInt("wins"),
                    rs.getInt("total_games")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // e.printStackTrace(); // 用户名重复会报错，实际开发中需区分处理
            return false;
        }
    }

    /**
     * 获取排行榜（前10名，按胜场排序）
     */
    public List<User> getLeaderboard() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY wins DESC LIMIT 10";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getInt("wins"),
                    rs.getInt("total_games")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStats(int userId, boolean isWin) {
        String sql = "UPDATE users SET wins = wins + ?, total_games = total_games + 1, win_rate = (wins/total_games)*100 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, isWin ? 1 : 0);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void recordGame(int p1Id, int p2Id, int winnerId) {
        String sql = "INSERT INTO games (player1_id, player2_id, winner_id, start_time) VALUES (?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, p1Id);
            pstmt.setInt(2, p2Id);
            pstmt.setInt(3, winnerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
