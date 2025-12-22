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

    // 结算游戏并写入数据库
    public void gameOver(String resultCmd) {
        
        redPlayer.sendMessage(Cmd.GAMEOVER + "|" + resultCmd);
        blackPlayer.sendMessage(Cmd.GAMEOVER + "|" + resultCmd);

        boolean redWins = resultCmd.equals(Cmd.RED_WIN);
        ClientHandler winner = redWins ? redPlayer : blackPlayer;
        ClientHandler loser = redWins ? blackPlayer : redPlayer;
        
        DBManager db = new DBManager();
        if (winner.getUser() != null && loser.getUser() != null) {
            db.recordGame(winner.getUser().getId(), loser.getUser().getId(), redWins ? 1 : 2); 
            db.updateStats(winner.getUser().getId(), true);
            db.updateStats(loser.getUser().getId(), false);
        }
    }
}
