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
            // 如果断开时还在对局中，则视为逃跑/掉线，触发判负
            if (currentSession != null) {
                currentSession.onAbnormalDisconnect(this);
            }

            try { socket.close(); } catch (IOException e) {}
            server.removeClient(this);
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
                        sendMessage(Cmd.CHAT + "|系统: 您当前不在对局中，无法发送聊天。尝试输入 /ranking 查看排行榜。");
                    }
                }
                break;
                
            case Cmd.GAMEOVER:
                if (currentSession != null) currentSession.gameOver(data); 
                break;
        }
    }

    // [修改] 修复因换行符导致客户端无法接收后续内容的问题
    private void handleChatCommand(String cmdStr) {
        String command = cmdStr.trim();
        
        if (command.equalsIgnoreCase("/ranking")) {
            System.out.println("[Cmd] 收到排行榜请求，正在查询数据库...");
            
            // 1. 获取数据
            List<User> top10 = new DBManager().getWinRateLeaderboard();
            System.out.println("[Cmd] 获取到排行榜数据条数: " + top10.size());
            
            // 2. [修改] 逐行发送消息，避免使用 \n 导致客户端 readLine() 截断
            sendMessage(Cmd.CHAT + "|系统: === 🏆 胜率排行榜 (Top 10) ===");
            
            if (top10.isEmpty()) {
                sendMessage(Cmd.CHAT + "|   (暂无对局数据)"); 
            } else {
                int rank = 1;
                for (User u : top10) {
                    // 格式化每一行
                    String line = String.format("   %d. %-6s | 胜率: %s | 场次: %d", 
                              rank++, 
                              u.getUsername(), 
                              u.getWinRateStr(), 
                              u.getTotalGames());
                    
                    // 每一行单独作为一个 CHAT 包发送
                    sendMessage(Cmd.CHAT + "|" + line);
                    
                    // [调试]
                    // System.out.println("[Cmd] 发送行: " + line);
                }
            }
            sendMessage(Cmd.CHAT + "|============================");
            System.out.println("[Cmd] 排行榜响应已分行发送完毕。");
            
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
