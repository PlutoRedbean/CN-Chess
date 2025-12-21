package Window;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField userField;
    private JPasswordField passField;
    private boolean isLogin = false; // 用户点击的是登录还是注册
    private boolean succeeded = false; // 操作是否成功提交

    public LoginDialog(Frame parent) {
        super(parent, "用户登录", true); // true 表示模态窗口，阻塞父窗口
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        cs.gridx = 0; cs.gridy = 0; cs.gridwidth = 1;
        panel.add(new JLabel("用户名: "), cs);

        cs.gridx = 1; cs.gridy = 0; cs.gridwidth = 2;
        userField = new JTextField(15);
        panel.add(userField, cs);

        // 密码
        cs.gridx = 0; cs.gridy = 1; cs.gridwidth = 1;
        panel.add(new JLabel("密  码: "), cs);

        cs.gridx = 1; cs.gridy = 1; cs.gridwidth = 2;
        passField = new JPasswordField(15);
        panel.add(passField, cs);

        // 按钮
        JButton btnLogin = new JButton("登录");
        JButton btnRegister = new JButton("注册");
        
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnRegister);

        btnLogin.addActionListener(e -> {
            isLogin = true;
            succeeded = true;
            dispose();
        });

        btnRegister.addActionListener(e -> {
            isLogin = false;
            succeeded = true;
            dispose();
        });

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public String getUsername() { return userField.getText().trim(); }
    public String getPassword() { return new String(passField.getPassword()); }
    public boolean isLoginAction() { return isLogin; }
    public boolean isSucceeded() { return succeeded; }
}
