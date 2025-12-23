package Window;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import Model.User;

public class SidePanel extends JPanel {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    private UserInfoPanel selfInfoPanel;     // 自己的信息面板
    private UserInfoPanel opponentInfoPanel; // 对手的信息面板
    private JPanel voicePanel;               // 语音控制面板
    private JButton loginButton;             // 重新定义的登录按钮（放在底部或顶部特定位置）
    private JLabel matchStatusLabel;         // 显示 "我 vs XXX"
    
    private JToggleButton btnMic;
    private JToggleButton btnSpeaker;

    private User currentUser;

    private Consumer<String> sendCallback;
    
    public SidePanel() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(240, 240, 240)); 
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initInfoPanel(); // 初始化顶部的信息区域
        initChatPanel(); // 初始化底部的聊天区域
    }

    private void initInfoPanel() {
        // 创建一个垂直布局的容器，放在 BorderLayout.NORTH
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // 1. 自己的信息
        selfInfoPanel = new UserInfoPanel("我 (Player)");
        topContainer.add(selfInfoPanel);
        
        // 添加一点间距
        topContainer.add(Box.createVerticalStrut(10));

        // 2. 对战状态条 (我 vs 对手) + 登录按钮
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        
        matchStatusLabel = new JLabel("未开始对局", JLabel.CENTER);
        matchStatusLabel.setFont(new Font("SimHei", Font.BOLD, 18));
        matchStatusLabel.setForeground(new Color(100, 100, 100));
        
        // 将登录按钮做的更小巧，放在这里
        loginButton = new JButton("登录");
        loginButton.setFocusPainted(false);

        statusPanel.add(matchStatusLabel, BorderLayout.CENTER);
        statusPanel.add(loginButton, BorderLayout.EAST);
        
        topContainer.add(statusPanel);
        topContainer.add(Box.createVerticalStrut(10));

        // 3. 对手的信息
        opponentInfoPanel = new UserInfoPanel("对手 (Opponent)");
        topContainer.add(opponentInfoPanel);
        
        // 4. 语音聊天控制区
        initVoicePanel(topContainer);

        add(topContainer, BorderLayout.NORTH);
    }

    private void initVoicePanel(JPanel container) {
        container.add(Box.createVerticalStrut(10));
        
        voicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voicePanel.setBorder(BorderFactory.createTitledBorder("语音聊天"));
        voicePanel.setOpaque(false);

        btnMic = new JToggleButton("麦克风: 关");
        btnSpeaker = new JToggleButton("扬声器: 开");

        // 简单的交互逻辑，仅改变文字，后续接入手册
        btnMic.addActionListener(e -> {
            boolean selected = btnMic.isSelected();
            btnMic.setText(selected ? "麦克风: 开" : "麦克风: 关");
            // TODO: 这里调用网络层的开启/关闭录音
        });

        btnSpeaker.addActionListener(e -> {
            boolean selected = btnSpeaker.isSelected();
            btnSpeaker.setText(selected ? "扬声器: 关" : "扬声器: 开"); // 逻辑取反，按下通常表示静音，视习惯而定
        });
        
        // 默认状态
        btnMic.setSelected(false);
        btnSpeaker.setSelected(false); // 假设默认是开着的

        voicePanel.add(btnMic);
        voicePanel.add(btnSpeaker);

        container.add(voicePanel);
    }

    public void setSendCallback(Consumer<String> callback) {
        this.sendCallback = callback;
    }

    public void setLoginAction(ActionListener action) {
        loginButton.addActionListener(action);
    }

    public void onLoginSuccess(User user) {
        this.currentUser = user;
        // 更新自己的面板
        selfInfoPanel.updateUser(user);
        
        loginButton.setText("已登录");
        loginButton.setEnabled(false);
        appendMessage("系统: 欢迎回来，" + user.getUsername());
    }
    
    public void onMatchStart(User opponent) {
        opponentInfoPanel.updateUser(opponent);
        matchStatusLabel.setText("VS");
        matchStatusLabel.setForeground(Color.RED);
        appendMessage("系统: 匹配成功！对手是 " + opponent.getUsername());
    }

    public void onMatchEnd() {
        opponentInfoPanel.reset();
        matchStatusLabel.setText("对局结束");
        matchStatusLabel.setForeground(Color.GRAY);
    }

    private void initChatPanel() {
        JPanel chatContainer = new JPanel(new BorderLayout(0, 5));
        chatContainer.setOpaque(false);
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("聊天室"));
        
        // 如果您希望聊天框显得“短”一点，给它设置一个 PreferredSize 是不够的，
        // 因为 BorderLayout.CENTER 会强制拉伸。
        // 但由于 North 区域现在加了很多东西（两个信息面板+语音面板），
        // 聊天框的高度自然会被压缩，符合您“缩小”的需求。
        
        chatContainer.add(scrollPane, BorderLayout.CENTER);

        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(new Font("SimHei", Font.PLAIN, 14));
        
        sendButton = new JButton("发送");
        // 可以稍微美化一下发送按钮
        sendButton.setBackground(new Color(60, 179, 113)); // 绿色风格
        sendButton.setForeground(Color.WHITE);
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatContainer.add(inputPanel, BorderLayout.SOUTH);

        add(chatContainer, BorderLayout.CENTER);

        ActionListener sendAction = e -> onSend();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
    }

    private void onSend() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        String senderName = (currentUser != null) ? currentUser.getUsername() : "我";
        appendMessage(senderName + ": " + text);

        if (sendCallback != null) {
            sendCallback.accept(text);
        }

        inputField.setText("");
    }

    public void appendMessage(String msg) {
        chatArea.append(msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void setMatchTitle(String title) {
        if (matchStatusLabel != null) {
            matchStatusLabel.setText(title);
        }
    }
}
