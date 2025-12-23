package Server;

import java.io.PrintWriter;

import Common.Cmd;

public class GameSession {
    public ClientHandler redPlayer;
    public ClientHandler blackPlayer;

    public GameSession(ClientHandler red, ClientHandler black) {
        this.redPlayer = red;
        this.blackPlayer = black;
    }

    // 转发移动指令
    public void forwardMove(ClientHandler sender, String moveData) {
        ClientHandler opponent = (sender == redPlayer) ? blackPlayer : redPlayer;
        if (opponent != null) {
            opponent.sendMessage("MOVE|" + moveData);
        }
    }

    // 转发聊天
    public void forwardChat(ClientHandler sender, String msg) {
        ClientHandler opponent = (sender == redPlayer) ? blackPlayer : redPlayer;
        if (opponent != null) {
            opponent.sendMessage("CHAT|" + sender.getUser().getUsername() + ": " + msg);
        }
    }

    // 处理玩家异常断线的方法
    public void onAbnormalDisconnect(ClientHandler loser) {
        // 1. 确定赢家（剩下的那个）
        ClientHandler winner = (loser == redPlayer) ? blackPlayer : redPlayer;
        
        if (winner != null) {
            System.out.println("玩家 " + loser.getUser().getUsername() + " 断开连接，判负。");
            
            // 2. 通知赢家
            winner.sendMessage(Cmd.CHAT + "|系统: 对方断开连接，你自动获胜！");
            
            // 3. 构造胜负结果
            String result = (winner == redPlayer) ? Cmd.RED_WIN : Cmd.BLACK_WIN;
            
            // 4. 调用通用的结算逻辑
            gameOver(result);
        }
    }

    // 结算游戏并写入数据库
    public void gameOver(String resultCmd) {
        // 发送结束指令 (如果一方已断开，sendMessage 内部通常不会抛出异常，只是发送失败)
        if (redPlayer != null) redPlayer.sendMessage(Cmd.GAMEOVER + "|" + resultCmd);
        if (blackPlayer != null) blackPlayer.sendMessage(Cmd.GAMEOVER + "|" + resultCmd);

        boolean redWins = resultCmd.equals(Cmd.RED_WIN);
        ClientHandler winner = redWins ? redPlayer : blackPlayer;
        ClientHandler loser = redWins ? blackPlayer : redPlayer;
        
        DBManager db = new DBManager();
        // 确保双方用户数据存在才记录（防止空指针）
        if (winner != null && loser != null && winner.getUser() != null && loser.getUser() != null) {
            db.recordGame(winner.getUser().getId(), loser.getUser().getId(), redWins ? 1 : 2); 
            db.updateStats(winner.getUser().getId(), true);
            db.updateStats(loser.getUser().getId(), false);
        }

        // 游戏结束后，解绑 session，避免玩家正常退出时触发断线判负
        if (redPlayer != null) redPlayer.setSession(null);
        if (blackPlayer != null) blackPlayer.setSession(null);
    }
}
