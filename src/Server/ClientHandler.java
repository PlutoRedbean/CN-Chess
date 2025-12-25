package Server;

import Common.Cmd;
import Model.User;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private GameServer server;
    private PrintWriter out;
    private BufferedReader in;
    private User user; // 当前登录用户
    private GameSession currentSession;

    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                String cmd = parts[0];
                String data = parts.length > 1 ? parts[1] : "";

                handleCommand(cmd, data);
            }
        } catch (IOException e) {
            System.out.println("检测到客户端异常断开: " + (user != null ? user.getUsername() : socket.getInetAddress()));
        } finally {
            // 1. 如果还在对局中，处理判负
            if (currentSession != null) {
                currentSession.onAbnormalDisconnect(this);
            }

            // 2. 如果用户已登录，从在线列表中移除
            if (user != null) {
                server.unregisterUser(user.getId());
            }

            // 3. 移除匹配队列引用并关闭Socket
            server.removeClient(this);
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private void handleCommand(String cmd, String data) {
        String[] parts = data.split("\\|");
        
        switch (cmd) {
            case Cmd.LOGIN:
                String[] auth = data.split("\\|");
                if(auth.length < 2) return;
                // 调用数据库验证
                User u = new DBManager().login(auth[0], auth[1]);
                if (u != null) {
                    // 如果 registerUser 返回 false，说明该ID已经在 map 中了
                    if (!server.registerUser(u, this)) {
                        sendMessage(Cmd.FAIL + "|登录失败：该账号已在别处登录");
                        return; // 结束本次处理，不赋值 this.user
                    }
                    
                    this.user = u;
                    
                    String userData = u.getId() + "," + u.getUsername() + "," + u.getWins() + "," + u.getTotalGames();
                    sendMessage(Cmd.LOGIN + "|SUCCESS|" + userData); 
                    
                    server.addToQueue(this); 
                } else {
                    sendMessage(Cmd.FAIL + "|登录失败，账号或密码错误");
                }
                break;
                
            case Cmd.REGISTER:
                if(parts.length < 2) return;
                boolean regSuccess = new DBManager().register(parts[0], parts[1]);
                if (regSuccess) {
                    sendMessage(Cmd.REGISTER + "|SUCCESS");
                } else {
                    sendMessage(Cmd.REGISTER + "|FAIL|用户名已存在");
                }
                break;
                
            case Cmd.MOVE:
                if (currentSession != null) currentSession.forwardMove(this, data);
                break;

            case Cmd.CHAT:
                if (data.startsWith("/")) {
                    handleChatCommand(data);
                } else {
                    if (currentSession != null) {
                        currentSession.forwardChat(this, data);
                    } else {
                        sendMessage(Cmd.CHAT + "|系统: 您当前不在对局中，无法发送聊天。");
                    }
                }
                break;
                
            case Cmd.GAMEOVER:
                if (currentSession != null) currentSession.gameOver(data); 
                break;
        }
    }

    // 修复因换行符导致客户端无法接收后续内容的问题
    private void handleChatCommand(String cmdStr) {
        String command = cmdStr.trim();
        
        if (command.equalsIgnoreCase("/ranking")) {
            System.out.println("[Cmd] 收到排行榜请求，正在查询数据库...");
            
            // 1. 获取数据
            List<User> top10 = new DBManager().getWinRateLeaderboard();
            System.out.println("[Cmd] 获取到排行榜数据条数: " + top10.size());
            
            // 2. 逐行发送消息，避免使用 \n 导致客户端 readLine() 截断
            sendMessage(Cmd.CHAT + "|系统: === 🏆 胜率排行榜 (Top 10) ===");
            
            if (top10.isEmpty()) {
                sendMessage(Cmd.CHAT + "|   (暂无对局数据)"); 
            } else {
                int rank = 1;
                for (User u : top10) {
                    String line = String.format("   %2d. %-12s | 胜率: %6s | 场次: %-5d", 
                              rank++, 
                              u.getUsername(), 
                              u.getWinRateStr(), 
                              u.getTotalGames());
                    
                    sendMessage(Cmd.CHAT + "|" + line);
                }
            }
            // 加长底部分隔符以匹配新的表格宽度
            sendMessage(Cmd.CHAT + "|===============================================");
            
        } else if (command.equalsIgnoreCase("/history")) {
            // 1. 检查是否登录
            if (this.user == null) {
                sendMessage(Cmd.CHAT + "|系统: 您尚未登录，无法查看历史记录。");
                return;
            }

            System.out.println("[Cmd] 收到历史记录查询: " + this.user.getUsername());
            
            // 2. 查询数据
            List<String> historyList = new DBManager().getHistory(this.user.getId());
            
            // 3. 构建并发送消息
            sendMessage(Cmd.CHAT + "|系统: === 📜 我的近15场对局 ===");
            
            if (historyList.isEmpty()) {
                sendMessage(Cmd.CHAT + "|   (暂无对局记录)");
            } else {
                for (String record : historyList) {
                    // 逐行发送
                    sendMessage(Cmd.CHAT + "|   " + record);
                }
            }
            sendMessage(Cmd.CHAT + "|============================");
            
        } else {
            sendMessage(Cmd.CHAT + "|系统: 未知指令 " + cmdStr);
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
    
    public void setSession(GameSession session) { this.currentSession = session; }
    public User getUser() { return user; }
}
