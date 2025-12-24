package Window;

import javax.sound.sampled.Mixer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.util.Vector;

import Model.User;
import Network.VoiceClient;

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

    private JComboBox<String> comboMic;
    private JComboBox<String> comboSpeaker;
    private JSlider sliderVolume;
    private VoiceClient voiceClient;
    private java.util.List<Mixer.Info> micList;
    private java.util.List<Mixer.Info> spkList;
    
    public SidePanel() {
        // [增加] 初始化语音客户端
        voiceClient = new VoiceClient();
        
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(240, 240, 240)); 
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initInfoPanel(); // 初始化顶部的信息区域
        initChatPanel(); // 初始化底部的聊天区域

        SwingUtilities.invokeLater(this::startVoiceService);
    }

    // [修改] 启动语音服务 (现在只初始化网络监听，不打开 Mic/Speaker)
    private void startVoiceService() {
        Mixer.Info mic = micList.isEmpty() ? null : micList.get(0);
        Mixer.Info spk = spkList.isEmpty() ? null : spkList.get(0);
        
        // 调用 init 而不是 start
        voiceClient.init(mic, spk);
        
        // 读取当前按钮状态来决定是否打开设备
        voiceClient.setMicEnabled(btnMic.isSelected());
        voiceClient.setSpeakerEnabled(btnSpeaker.isSelected());
        
        System.out.println("语音服务已初始化，等待开关操作");
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
        
        // 使用垂直 BoxLayout 容纳更多控件
        voicePanel = new JPanel();
        voicePanel.setLayout(new BoxLayout(voicePanel, BoxLayout.Y_AXIS));
        voicePanel.setBorder(BorderFactory.createTitledBorder("语音聊天设置"));
        voicePanel.setOpaque(false);

        // --- 第一行：开关按钮 ---
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        switchPanel.setOpaque(false);
        
        btnMic = new JToggleButton("麦克风: 关");
        btnSpeaker = new JToggleButton("扬声器: 关");
        btnMic.setSelected(false); // 默认关闭接收
        btnSpeaker.setSelected(false); // 默认关闭接收

        switchPanel.add(btnMic);
        switchPanel.add(btnSpeaker);
        
        // --- 第二行：设备选择 ---
        JPanel devicePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        devicePanel.setOpaque(false);
        devicePanel.setBorder(new EmptyBorder(0, 5, 0, 5));

        // 获取设备列表
        micList = voiceClient.getAudioInputDevices();
        spkList = voiceClient.getAudioOutputDevices();

        Vector<String> micNames = new Vector<>();
        for(Mixer.Info info : micList) micNames.add("Mic: " + info.getName());
        // 如果没找到设备，给个默认值
        if(micNames.isEmpty()) micNames.add("默认麦克风");

        Vector<String> spkNames = new Vector<>();
        for(Mixer.Info info : spkList) spkNames.add("Spk: " + info.getName());
        if(spkNames.isEmpty()) spkNames.add("默认扬声器");

        comboMic = new JComboBox<>(micNames);
        comboSpeaker = new JComboBox<>(spkNames);
        // 稍微调小一点字体以免太宽
        comboMic.setFont(new Font("Dialog", Font.PLAIN, 10));
        comboSpeaker.setFont(new Font("Dialog", Font.PLAIN, 10));

        devicePanel.add(comboMic);
        devicePanel.add(comboSpeaker);

        // --- 第三行：音量滑块 ---
        JPanel volumePanel = new JPanel(new BorderLayout());
        volumePanel.setOpaque(false);
        volumePanel.setBorder(new EmptyBorder(5, 5, 0, 5));
        JLabel lblVol = new JLabel("增益: ");
        lblVol.setFont(new Font("Dialog", Font.PLAIN, 10));
        
        // 滑块范围 0 ~ 200， 默认 100 (代表 1.0)
        sliderVolume = new JSlider(0, 200, 100);
        sliderVolume.setOpaque(false);
        sliderVolume.setToolTipText("自动音量增益调节");
        
        volumePanel.add(lblVol, BorderLayout.WEST);
        volumePanel.add(sliderVolume, BorderLayout.CENTER);

        // --- 添加所有组件到 voicePanel ---
        voicePanel.add(switchPanel);
        voicePanel.add(devicePanel);
        voicePanel.add(volumePanel);

        container.add(voicePanel);

        // --- [修改] 事件监听逻辑 ---
        
        btnMic.addActionListener(e -> {
            boolean selected = btnMic.isSelected();
            btnMic.setText(selected ? "麦克风: 开" : "麦克风: 关");
            // 实时控制 VoiceClient
            voiceClient.setMicEnabled(selected);
        });

        btnSpeaker.addActionListener(e -> {
            boolean selected = btnSpeaker.isSelected();
            btnSpeaker.setText(selected ? "扬声器: 关" : "扬声器: 开"); // 按钮文字逻辑
            // 实时控制 VoiceClient
            voiceClient.setSpeakerEnabled(!selected); // 注意：如果按钮显示"关"，意味着静音，所以 enabled = !selected
        });
        
        // 设备切换监听 (重启服务以应用新设备)
        ActionListener restartVoiceAction = e -> {
            // 获取新设备
            int micIdx = comboMic.getSelectedIndex();
            int spkIdx = comboSpeaker.getSelectedIndex();
            Mixer.Info micInfo = (micIdx >= 0 && micIdx < micList.size()) ? micList.get(micIdx) : null;
            Mixer.Info spkInfo = (spkIdx >= 0 && spkIdx < spkList.size()) ? spkList.get(spkIdx) : null;
            
            // 更新设备配置 (内部会自动处理资源的释放和重连)
            voiceClient.setDevice(micInfo, spkInfo);
        };

        comboMic.addActionListener(restartVoiceAction);
        comboSpeaker.addActionListener(restartVoiceAction);

        // 音量调节监听
        sliderVolume.addChangeListener(e -> {
            float gain = sliderVolume.getValue() / 100.0f;
            voiceClient.setVolume(gain);
        });
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
