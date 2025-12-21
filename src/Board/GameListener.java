package Board;

public interface GameListener {
    /**
     * 当游戏结束时调用
     * @param redWins true表示红方胜，false表示黑方胜
     */
    void onGameOver(boolean redWins);

    // 未来可以在这里添加 onMessageReceived(String msg) 等用于聊天
}
