package Server;

import Common.VoiceConfig;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

// [修改] 去掉 main 方法，改为普通类，由 GameServer 启动
public class VoiceServer {
    private Set<String> clients = new HashSet<>(); 
    private boolean isRunning = true;

    public void start() {
        System.out.println("=== 语音服务器(UDP) 准备启动, 端口: " + VoiceConfig.SERVER_PORT + " ===");
        
        try (DatagramSocket socket = new DatagramSocket(VoiceConfig.SERVER_PORT)) {
            byte[] buffer = new byte[VoiceConfig.BUFFER_SIZE];
            System.out.println("=== 语音服务器(UDP) 启动成功 ===");

            while (isRunning) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String clientKey = packet.getAddress().getHostAddress() + ":" + packet.getPort();
                
                // 1. 注册/更新用户
                if (!clients.contains(clientKey)) {
                    clients.add(clientKey);
                    System.out.println("[VoiceServer] 新客户端: " + clientKey);
                }

                // 2. 转发逻辑 (过滤掉过小的心跳包)
                if (packet.getLength() > 100) {
                    for (String client : clients) {
                        if (!client.equals(clientKey)) {
                            try {
                                String[] parts = client.split(":");
                                InetAddress address = InetAddress.getByName(parts[0]);
                                int port = Integer.parseInt(parts[1]);

                                DatagramPacket forwardPacket = new DatagramPacket(
                                        packet.getData(),
                                        packet.getLength(),
                                        address,
                                        port
                                );
                                socket.send(forwardPacket);
                            } catch (Exception e) {
                                clients.remove(client); // 移除失效客户端
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("语音服务器启动失败 (端口可能被占用): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
