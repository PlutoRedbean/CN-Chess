package Network;

import Common.VoiceConfig;

import javax.sound.sampled.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class VoiceClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    
    private TargetDataLine microphone;
    private Thread captureThread;
    private volatile boolean isCapturing = false;
    
    private SourceDataLine speaker;
    private Thread playbackThread;
    private volatile boolean isPlaybackRunning = false;
    private volatile boolean isSpeakerEnabled = false;

    private Mixer.Info currentMicInfo;
    private Mixer.Info currentSpeakerInfo;

    private float masterGain = 1.0f; 

    // 统计计数器，用于减少日志刷屏频率
    private int sendLogCounter = 0;
    private int receiveLogCounter = 0;

    private Thread heartbeatThread;
    private volatile boolean isHeartbeatRunning = false;

    public VoiceClient() {
        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(VoiceConfig.SERVER_IP);
            System.out.println("[Voice-Init] UDP Socket created on dynamic port.");
        } catch (Exception e) {
            System.err.println("[Voice-Error] Socket creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Mixer.Info> getAudioInputDevices() {
        List<Mixer.Info> inputs = new ArrayList<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (mixer.isLineSupported(new Line.Info(TargetDataLine.class))) {
                inputs.add(info);
            }
        }
        return inputs;
    }
    
    public List<Mixer.Info> getAudioOutputDevices() {
        List<Mixer.Info> outputs = new ArrayList<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (mixer.isLineSupported(new Line.Info(SourceDataLine.class))) {
                outputs.add(info);
            }
        }
        return outputs;
    }

    public void init(Mixer.Info micInfo, Mixer.Info speakerInfo) {
        this.currentMicInfo = micInfo;
        this.currentSpeakerInfo = speakerInfo;
        System.out.println("[Voice-Setup] Mic: " + (micInfo != null ? micInfo.getName() : "Auto"));
        System.out.println("[Voice-Setup] Spk: " + (speakerInfo != null ? speakerInfo.getName() : "Auto"));

        // 1. 启动播放监听线程 (保持不变)
        if (!isPlaybackRunning) {
            isPlaybackRunning = true;
            playbackThread = new Thread(this::runPlaybackLoop);
            playbackThread.start();
            System.out.println("[Voice-System] Playback thread started.");
        }

        // 2. 启动心跳线程 (确保服务端知道我的存在)
        if (!isHeartbeatRunning) {
            isHeartbeatRunning = true;
            heartbeatThread = new Thread(this::runHeartbeatLoop);
            heartbeatThread.start();
            System.out.println("[Voice-System] Heartbeat thread started.");
        }
    }

    private void runHeartbeatLoop() {
        byte[] pingData = "PING".getBytes();
        DatagramPacket pingPacket = new DatagramPacket(pingData, pingData.length, serverAddress, VoiceConfig.SERVER_PORT);

        while (isHeartbeatRunning) {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.send(pingPacket);
                    // System.out.println("[Voice-Heartbeat] Sent PING."); // 调试用，嫌烦可注释
                }
                Thread.sleep(3000); // 3秒发送一次
            } catch (InterruptedException ie) {
                break;
            } catch (Exception e) {
                System.err.println("[Voice-Heartbeat] Error: " + e.getMessage());
            }
        }
    }

    public synchronized void setMicEnabled(boolean enabled) {
        System.out.println("[Voice-Control] Request set Mic: " + enabled);
        if (enabled) {
            if (isCapturing) return;
            isCapturing = true;
            captureThread = new Thread(this::runCaptureLoop);
            captureThread.start();
        } else {
            if (!isCapturing) return;
            isCapturing = false;
            if (microphone != null && microphone.isOpen()) {
                microphone.close(); 
            }
        }
    }

    public void setSpeakerEnabled(boolean enabled) {
        System.out.println("[Voice-Control] Request set Speaker: " + enabled);
        this.isSpeakerEnabled = enabled;
        if (!enabled) {
            closeSpeakerLine();
        } 
    }
    
    public void setDevice(Mixer.Info micInfo, Mixer.Info speakerInfo) {
        System.out.println("[Voice-Config] Switching devices...");
        boolean wasMicOn = isCapturing;
        setMicEnabled(false);
        closeSpeakerLine();
        
        this.currentMicInfo = micInfo;
        this.currentSpeakerInfo = speakerInfo;
        
        if (wasMicOn) setMicEnabled(true);
    }

    public void setVolume(float gain) {
        this.masterGain = gain;
        System.out.println("[Voice-Config] Volume gain set to: " + gain);
    }

    // --- 内部逻辑 ---

    private void runCaptureLoop() {
        System.out.println("[Voice-Mic] Capture thread started.");
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (currentMicInfo != null) {
                microphone = (TargetDataLine) AudioSystem.getMixer(currentMicInfo).getLine(info);
            } else {
                microphone = (TargetDataLine) AudioSystem.getLine(info);
            }

            microphone.open(format);
            microphone.start();
            System.out.println("[Voice-Mic] Hardware OPEN Success. Buffer Size: " + microphone.getBufferSize());

            byte[] buffer = new byte[VoiceConfig.BUFFER_SIZE];

            while (isCapturing) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    double volume = calculateRMSVolume(buffer);
                    
                    sendLogCounter++;
                    if (sendLogCounter % 20 == 0 || volume > 20) {
                        //  System.out.printf("[Voice-Mic] Read %d bytes | Vol: %.2f | Sending to %s:%d\n", 
                            //  bytesRead, volume, VoiceConfig.SERVER_IP, VoiceConfig.SERVER_PORT);
                    }

                    DatagramPacket packet = new DatagramPacket(buffer, bytesRead, serverAddress, VoiceConfig.SERVER_PORT);
                    socket.send(packet);
                }
            }
        } catch (Exception e) {
            if (isCapturing) { 
                System.err.println("[Voice-Mic-Error] " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            if (microphone != null && microphone.isOpen()) microphone.close();
            System.out.println("[Voice-Mic] Hardware CLOSED.");
        }
    }

    private void runPlaybackLoop() {
        System.out.println("[Voice-Net] Network Listener thread started.");
        byte[] buffer = new byte[VoiceConfig.BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (isPlaybackRunning) {
            try {
                // 1. 接收数据
                socket.receive(packet);
                int len = packet.getLength();
                
                receiveLogCounter++;
                if (receiveLogCounter % 20 == 0) {
                    // System.out.printf("[Voice-Net] Received packet from %s | Size: %d | SpeakerEnabled: %b\n", 
                        // packet.getAddress().getHostAddress(), len, isSpeakerEnabled);
                }

                if (isSpeakerEnabled) {
                    if (speaker == null || !speaker.isOpen()) {
                        System.out.println("[Voice-Spk] Attempting to open speaker line...");
                        openSpeakerLine();
                    }
                    
                    if (speaker != null && speaker.isOpen()) {
                        byte[] audioData = packet.getData();
                        
                        double volumeBefore = calculateRMSVolume(audioData); // 仅计算前1024字节作为参考
                        
                        if (masterGain != 1.0f) {
                            adjustVolume(audioData, masterGain);
                        }
                        
                        speaker.write(audioData, 0, len);
                        
                        if (volumeBefore > 5 && receiveLogCounter % 10 == 0) {
                            //  System.out.printf("[Voice-Spk] Writing audio. Raw Vol: %.2f\n", volumeBefore);
                        }
                    } else {
                        // Speaker failed to open
                        if (receiveLogCounter % 50 == 0) System.err.println("[Voice-Spk] Speaker line is NOT open!");
                    }
                } 
            } catch (Exception e) {
                if (isPlaybackRunning) {
                    System.err.println("[Voice-Net-Error] " + e.getMessage());
                }
            }
        }
        System.out.println("[Voice-Net] Network Listener thread stopped.");
    }

    private void openSpeakerLine() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            
            if (currentSpeakerInfo != null) {
                speaker = (SourceDataLine) AudioSystem.getMixer(currentSpeakerInfo).getLine(info);
            } else {
                speaker = (SourceDataLine) AudioSystem.getLine(info);
            }
            
            speaker.open(format);
            speaker.start();
            System.out.println("[Voice-Spk] Hardware OPEN Success.");
        } catch (Exception e) {
            System.err.println("[Voice-Spk-Error] Failed to open: " + e.getMessage());
        }
    }

    private void closeSpeakerLine() {
        if (speaker != null) {
            speaker.close(); 
            speaker = null;  
            System.out.println("[Voice-Spk] Hardware CLOSED.");
        }
    }

    private void adjustVolume(byte[] audioData, float gain) {
        for (int i = 0; i < audioData.length; i += 2) {
            short sample = (short) ((audioData[i+1] << 8) | (audioData[i] & 0xff));
            sample = (short) (sample * gain);
            audioData[i] = (byte) (sample & 0xff);
            audioData[i+1] = (byte) ((sample >> 8) & 0xff);
        }
    }

    private double calculateRMSVolume(byte[] audioData) {
        long sum = 0;
        for (int i = 0; i < audioData.length; i += 2) {
            if (i+1 >= audioData.length) break;
            short sample = (short) ((audioData[i+1] << 8) | (audioData[i] & 0xff));
            sum += sample * sample;
        }
        double mean = sum / (audioData.length / 2.0);
        return Math.sqrt(mean);
    }

    private AudioFormat getAudioFormat() {
        return new AudioFormat(
                VoiceConfig.SAMPLE_RATE,
                VoiceConfig.SAMPLE_SIZE_IN_BITS,
                VoiceConfig.CHANNELS,
                VoiceConfig.SIGNED,
                VoiceConfig.BIG_ENDIAN
        );
    }
    
    public void stop() {
        isCapturing = false;
        isPlaybackRunning = false;
        isHeartbeatRunning = false; // 停止心跳
        
        setMicEnabled(false);
        closeSpeakerLine();
        
        if (socket != null && !socket.isClosed()) socket.close();
    }
}
