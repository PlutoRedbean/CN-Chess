package Server;

import Common.Cmd;
import Model.User;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private Map<Integer, ClientHandler> onlineUsers = new ConcurrentHashMap<>();
    private Queue<ClientHandler> waitQueue = new LinkedList<>();

    public void start(int port) throws Exception {
        // [增加] 启动语音服务器线程 (不会阻塞主线程)
        new Thread(() -> {
            new VoiceServer().start();
        }).start();
        
        // --- 原有的 TCP 游戏服务器启动逻辑 ---
        ServerSocket ss = new ServerSocket(port);
        System.out.println("游戏服务器(TCP) 启动在端口: " + port);

        while (true) {
            Socket socket = ss.accept();
            new ClientHandler(socket, this).start();
        }
    }

    // 返回 true 表示注册成功（之前不在线），返回 false 表示失败（已经在线）
    public synchronized boolean registerUser(User user, ClientHandler handler) {
        if (onlineUsers.containsKey(user.getId())) {
            System.out.println("用户重复登录被拒绝: " + user.getUsername());
            return false;
        }
        onlineUsers.put(user.getId(), handler);
        return true;
    }

    // 用户下线时移除状态
    public synchronized void unregisterUser(int userId) {
        if (onlineUsers.containsKey(userId)) {
            onlineUsers.remove(userId);
            System.out.println("用户下线移除: ID " + userId);
        }
    }

    // 简单的匹配逻辑
    public synchronized void addToQueue(ClientHandler player) {
        System.out.println("玩家加入队列: " + player.getUser().getUsername());
        waitQueue.add(player);
        checkMatch();
    }

    public synchronized void removeClient(ClientHandler player) {
        waitQueue.remove(player);
    }

    private void checkMatch() {
        if (waitQueue.size() >= 2) {
            ClientHandler p1 = waitQueue.poll();
            ClientHandler p2 = waitQueue.poll();

            System.out.println("匹配成功: " + p1.getUser().getUsername() + " vs " + p2.getUser().getUsername());

            GameSession session = new GameSession(p1, p2);
            p1.setSession(session);
            p2.setSession(session);

            // 注意：这里需要确保 User 对象不为空，登录逻辑保证了这一点
            String p1Data = p1.getUser().getId() + "," + p1.getUser().getUsername() + "," + 
                            p1.getUser().getWins() + "," + p1.getUser().getTotalGames();
            
            String p2Data = p2.getUser().getId() + "," + p2.getUser().getUsername() + "," + 
                            p2.getUser().getWins() + "," + p2.getUser().getTotalGames();

            // 格式: MATCH | 颜色 | 对手信息
            p1.sendMessage(Cmd.MATCH + "|RED|" + p2Data);   // 告诉 p1 对手是 p2
            p2.sendMessage(Cmd.MATCH + "|BLACK|" + p1Data); // 告诉 p2 对手是 p1
        }
    }

    public static void main(String[] args) throws Exception {
        new GameServer().start(81);
    }
}
