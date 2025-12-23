package Network;

import Common.Cmd;
import Model.User;
import Window.Window;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.*;
import java.net.Socket;

public class NetworkClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Window window; // 持有Window引用以便更新UI

    private static final String SERVER_IP = "www.lhh-redbean.cn"; 
    private static final int SERVER_PORT = 81;

    public NetworkClient(Window window) {
        this.window = window;
    }

    public void connect() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                SwingUtilities.invokeLater(() -> 
                    window.getSidePanel().appendMessage("系统: 已连接服务器，请登录")
                );

                String line;
                while ((line = in.readLine()) != null) {
                    processMessage(line);
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> 
                    window.getSidePanel().appendMessage("系统: 连接服务器失败")
                );
                e.printStackTrace();
            }
        }).start();
    }

    public void sendLogin(String user, String pass) {
        if (out != null) out.println(Cmd.LOGIN + "|" + user + "|" + pass);
    }

    public void sendRegister(String user, String pass) {
        if (out != null) out.println(Cmd.REGISTER + "|" + user + "|" + pass);
    }

    public void sendMove(int r1, int c1, int r2, int c2) {
        if (out != null) out.println(Cmd.MOVE + "|" + r1 + "," + c1 + "," + r2 + "," + c2);
    }

    public void sendChat(String msg) {
        if (out != null) out.println(Cmd.CHAT + "|" + msg);
    }
    
    public void sendWin(String whoWins) {
        if (out != null) out.println(Cmd.GAMEOVER + "|" + whoWins);
    }

    private void processMessage(String msg) {
        String[] parts = msg.split("\\|", 2);
        String cmd = parts[0];
        String data = parts.length > 1 ? parts[1] : "";

        SwingUtilities.invokeLater(() -> {
            switch (cmd) {
                case Cmd.LOGIN:
                    if (data.startsWith("SUCCESS")) {
                        // data 格式: SUCCESS|id,username,wins,totalGames
                        String[] args = data.split("\\|");
                        if (args.length > 1) {
                            String[] uInfo = args[1].split(",");
                            User me = new User(
                                Integer.parseInt(uInfo[0]), // id
                                uInfo[1],                   // username
                                Integer.parseInt(uInfo[2]), // wins
                                Integer.parseInt(uInfo[3])  // totalGames
                            );
                            window.getSidePanel().onLoginSuccess(me);
                        }
                        window.getSidePanel().appendMessage("系统: 登录成功，正在匹配对手...");
                    }
                    break;
                
                case Cmd.REGISTER:
                    if (data.startsWith("SUCCESS")) {
                        window.getSidePanel().appendMessage("系统: 注册成功，请重新登录。");
                        JOptionPane.showMessageDialog(window, "注册成功！请登录。");
                    } else {
                        JOptionPane.showMessageDialog(window, "注册失败：" + data);
                    }
                    break;
                
                case Cmd.FAIL:
                    window.getSidePanel().appendMessage("系统错误: " + data);
                    break;
                
                case Cmd.MATCH:
                    // data 格式: RED|id,username,wins,totalGames (这是对手的信息)
                    String[] matchArgs = data.split("\\|");
                    String colorStr = matchArgs[0];
                    boolean isRed = colorStr.equals("RED");
                    
                    if (matchArgs.length > 1) {
                        String[] uInfo = matchArgs[1].split(",");
                        User opponent = new User(
                            Integer.parseInt(uInfo[0]),
                            uInfo[1],
                            Integer.parseInt(uInfo[2]),
                            Integer.parseInt(uInfo[3])
                        );
                        window.getSidePanel().onMatchStart(opponent);
                    }

                    window.startGame(isRed);
                    window.getSidePanel().setMatchTitle("对战中"); 
                    window.getSidePanel().appendMessage("系统: 对战开始！你是 " + (isRed ? "红方" : "黑方"));
                    break;
                
                case Cmd.MOVE:
                    String[] pos = data.split(",");
                    window.getBoard().netMovePiece(
                        Integer.parseInt(pos[0]), Integer.parseInt(pos[1]),
                        Integer.parseInt(pos[2]), Integer.parseInt(pos[3])
                    );
                    break;
                
                case Cmd.CHAT:
                    window.getSidePanel().appendMessage(data);
                    break;
                
                case Cmd.GAMEOVER:
                    boolean redWins = data.equals(Cmd.RED_WIN);
                    window.onGameOver(redWins); 
                    window.getSidePanel().onMatchEnd();
                    break;
            }
        });
    }
}
