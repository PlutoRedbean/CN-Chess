package Common;

public class Cmd {
    public static final String LOGIN = "LOGIN";         // 登录: LOGIN|user|pass
    public static final String REGISTER = "REGISTER";   // 注册: REGISTER|user|pass
    public static final String CHAT = "CHAT";           // 聊天: CHAT|content
    public static final String MOVE = "MOVE";           // 走棋: MOVE|r1,c1,r2,c2
    public static final String MATCH = "MATCH";         // 匹配成功: MATCH|RED 或 MATCH|BLACK
    public static final String GAMEOVER = "GAMEOVER";   // 结束: GAMEOVER|WIN 或 GAMEOVER|LOSE
    public static final String FAIL = "FAIL";           // 失败/错误: FAIL|Reason
    public static final String RED_WIN = "RED_WIN";     // 红方胜利
    public static final String BLACK_WIN = "BLACK_WIN"; // 黑方胜利
}
