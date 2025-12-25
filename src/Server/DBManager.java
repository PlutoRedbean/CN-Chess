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

    public List<User> getWinRateLeaderboard() {
        List<User> list = new ArrayList<>();
        // 简化 SQL：只筛选有过对局的玩家，不进行复杂计算和排序，避免 SQL 兼容性问题
        String sql = "SELECT * FROM users WHERE total_games > 0";
        
        System.out.println("[DB] 正在执行排行榜查询 SQL: " + sql);

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User u = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getInt("wins"),
                    rs.getInt("total_games")
                );
                list.add(u);
                // [调试] 打印查到的原始数据
                System.out.println("[DB] 查到用户: " + u.getUsername() + " | 胜: " + u.getWins() + " | 总: " + u.getTotalGames());
            }
        } catch (SQLException e) {
            System.err.println("[DB-Error] 查询排行榜失败: ");
            e.printStackTrace();
        }
        
        System.out.println("[DB] SQL 查询结束，共找到 " + list.size() + " 条记录。正在进行内存排序...");

        // 在 Java 内存中排序：胜率优先(降序)，胜场次之(降序)
        list.sort((u1, u2) -> {
            double rate1 = (double) u1.getWins() / u1.getTotalGames();
            double rate2 = (double) u2.getWins() / u2.getTotalGames();
            
            // 胜率不同，高的排前
            if (Math.abs(rate1 - rate2) > 0.0001) {
                return Double.compare(rate2, rate1); 
            }
            // 胜率相同，胜场多的排前
            return Integer.compare(u2.getWins(), u1.getWins());
        });

        // 只取前 10 名
        if (list.size() > 10) {
            return list.subList(0, 10);
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
