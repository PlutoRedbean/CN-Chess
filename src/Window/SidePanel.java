package Window;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class SidePanel extends JPanel {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel statusLabel;

    public SidePanel() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(240, 240, 240)); // 浅灰色背景
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initTopPanel();
        initChatPanel();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);

        JLabel title = new JLabel("中国象棋 Online", JLabel.CENTER);
        title.setFont(new Font("SimHei", Font.BOLD, 18));
        
        statusLabel = new JLabel("等待连接...", JLabel.CENTER);
        statusLabel.setForeground(Color.DARK_GRAY);

        topPanel.add(title);
        topPanel.add(statusLabel);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initChatPanel() {
        chatArea = new JTextArea();
        chatArea.setEditable(false); // 禁止用户直接修改历史记录
        chatArea.setLineWrap(true);  // 自动换行
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("聊天室"));
        
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(new Font("SimHei", Font.PLAIN, 14));
        
        sendButton = new JButton("发送");
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        ActionListener sendAction = e -> onSend();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction); // 回车也能发送
    }

    private void onSend() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        // 暂时先自己显示给自己，未来这里会调用网络接口发送给服务器
        appendMessage("我: " + text);
        inputField.setText("");
    }

    public void appendMessage(String msg) {
        chatArea.append(msg + "\n");
        // 自动滚动到底部
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}
