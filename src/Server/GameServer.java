package Server;

import Common.Cmd;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class GameServer {
    private Queue<ClientHandler> waitQueue = new LinkedList<>();

    public void start(int port) throws Exception {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("服务器启动在端口: " + port);

        while (true) {
            Socket socket = ss.accept();
            new ClientHandler(socket, this).start();
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

            // 通知客户端开始，p1红方先手，p2黑方后手
            p1.sendMessage(Cmd.MATCH + "|RED");
            p2.sendMessage(Cmd.MATCH + "|BLACK");
        }
    }

    public static void main(String[] args) throws Exception {
        new GameServer().start(81);
    }
}
