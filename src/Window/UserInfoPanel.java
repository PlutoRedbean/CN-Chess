package Window;

import Model.User;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class UserInfoPanel extends JPanel {
    private JLabel lblUsername;
    private JLabel lblId;
    private JLabel lblStats; // 胜场、总局数、胜率
    private String roleTitle; // "我" 或 "对手"

    public UserInfoPanel(String title) {
        this.roleTitle = title;
        setLayout(new GridLayout(3, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            title, 
            TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, 
            new Font("SimHei", Font.BOLD, 14), 
            new Color(50, 50, 50)
        ));
        setBackground(new Color(250, 250, 250));

        initUI();
    }

    private void initUI() {
        lblUsername = new JLabel("未登录/未连接");
        lblUsername.setFont(new Font("SimHei", Font.BOLD, 16));
        lblUsername.setForeground(Color.DARK_GRAY);

        lblId = new JLabel("ID: --");
        lblId.setFont(new Font("Monospaced", Font.PLAIN, 12));

        lblStats = new JLabel("胜率: --% (0胜/0局)");
        lblStats.setFont(new Font("SimHei", Font.PLAIN, 12));

        add(lblUsername);
        add(lblId);
        add(lblStats);
    }

    /**
     * 更新用户信息
     * @param user 用户对象，如果为 null 则重置显示
     */
    public void updateUser(User user) {
        if (user == null) {
            reset();
            return;
        }
        lblUsername.setText(user.getUsername());
        lblId.setText("ID: " + user.getId());
        lblStats.setText(String.format("胜率: %s (%d胜/%d局)", 
            user.getWinRateStr(), user.getWins(), user.getTotalGames()));
        
        // 根据胜率改变颜色，增加视觉趣味
        if (user.getWins() > 0 && (double)user.getWins()/user.getTotalGames() > 0.5) {
            lblStats.setForeground(new Color(200, 0, 0)); // 胜率高显示红色
        } else {
            lblStats.setForeground(Color.BLACK);
        }
    }

    public void reset() {
        lblUsername.setText(roleTitle.equals("我") ? "未登录" : "等待匹配...");
        lblId.setText("ID: --");
        lblStats.setText("暂无数据");
        lblStats.setForeground(Color.GRAY);
    }
}
