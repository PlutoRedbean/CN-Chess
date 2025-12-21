package Window;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import Board.Board;
import Board.GameListener;
import Piece.Piece;
import Window.LoginDialog;

public class Window extends JFrame implements GameListener {
    private int width;
    private int height;
    private Board board;

    private SidePanel sidePanel;
    private JSplitPane splitPane;

    public Window(int width, int height) {
        this.width = width;
        this.height = height;
        window_init();
    }

    public Window() {
        this(500, 600);
    }

    private void window_init() {
        setTitle("中国象棋");
        setSize(width, height);

        setLocationRelativeTo(null); 

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        board = new Board();
        board.setGameListener(this);

        sidePanel = new SidePanel();

        sidePanel.setLoginAction(e -> handleLoginRequest());
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, board, sidePanel);

        splitPane.setResizeWeight(1.0); // 1.0 表示：当窗口变大时，新增的空间全部给左边(棋盘)
        
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);

        setVisible(true);

        splitPane.setDividerLocation(0.75);
        
        sidePanel.appendMessage("系统: 欢迎来到中国象棋！");
    }

    private void handleLoginRequest() {
        LoginDialog dialog = new LoginDialog(this);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            String user = dialog.getUsername();
            String pass = dialog.getPassword();
            boolean isLogin = dialog.isLoginAction();

            System.out.println("用户尝试: " + (isLogin ? "登录" : "注册"));
            System.out.println("账号: " + user + ", 密码: " + pass);
            
            // 模拟登录成功效果 (仅供UI测试)
            if (!user.isEmpty()) {
                Model.User mockUser = new Model.User(1, user, 5, 10); 
                sidePanel.onLoginSuccess(mockUser);
            } else {
                JOptionPane.showMessageDialog(this, "用户名不能为空");
            }
        }
    }

    /**
     * 把 Piece 对象放到它指定的 row,col
     * Piece 必须包含 row/col（0..9, 0..8）
     */
    public void add_piece(Piece p) {
        if (p == null) return;
        board.setPiece(p.getRow(), p.getCol(), p);
    }

    public void remove_piece(int row, int col) {
        board.setPiece(row, col, null);
    }

    @Override
    public void onGameOver(boolean redWins) {
        String winner = redWins ? "红方" : "黑方";

        sidePanel.appendMessage("系统: 游戏结束，" + winner + "获胜！");
        sidePanel.setStatus("游戏结束 - " + winner + "胜");

        JOptionPane.showMessageDialog(this, 
            "游戏结束！\n" + winner + " 获胜！", 
            "获胜通知", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
