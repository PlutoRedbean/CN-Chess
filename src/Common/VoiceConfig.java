package Common;

// [增加] 新增文件：语音聊天配置常量
public class VoiceConfig {
    // 语音服务器配置
    public static final String SERVER_IP = "www.lhh-redbean.cn"; // 你的语音服务器IP
    public static final int SERVER_PORT = 1234;       // 语音服务端口

    // 音频格式配置
    public static final float SAMPLE_RATE = 41000.0f; // 采样率 41kHz (人声足够)
    public static final int SAMPLE_SIZE_IN_BITS = 16; // 量化位数
    public static final int CHANNELS = 1;             // 单声道
    public static final boolean SIGNED = true;        // 有符号
    public static final boolean BIG_ENDIAN = false;   // 小端模式

    // 缓冲区大小 (决定延迟，越小延迟越低但越容易卡顿)
    public static final int BUFFER_SIZE = 1024;
}
