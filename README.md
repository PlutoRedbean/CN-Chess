# 联网中国象棋 (Online Chinese Chess)

（李泓浩 SUAT24000146 Java 课程大作业）
这是一个基于 Java Swing 和 Socket 编程实现的联网中国象棋对战系统。支持账号注册登录、实时匹配对战、排行榜查看、历史战绩回顾以及实时语音聊天功能。

## 功能特性
* **用户系统**：支持用户注册、登录，包含防重复登录机制。
* **匹配系统**：服务器维护等待队列，自动匹配两名玩家进入对局。
* **实时对战**：同步棋盘走子信息，自动判定胜负。
* **语音聊天**：基于 UDP 协议的实时语音通话，支持麦克风/扬声器动态切换。
* **数据统计**：根据胜率和胜场计算排行榜，记录最近 15 场历史对局。
* **聊天指令**：通过聊天中输入"/"开头的指令，展示排行榜（/ranking）和历史对局(/history)
* 所有必做功能/加分功能均完成

## 快速开始

### 1. 启动服务器

该部分代码主要为 Server 文件夹下的部分，在本项目中运行在远端个人服务器上
**（平时不开启运行，需要运行请联系微信：JumbleBeadsL）**

如需本地运行服务端 **（这需要修改代码内IP地址，不推荐）**,修改本地IP后，运行

```bash
$ javac -d bin -encoding UTF-8 -cp "lib/*:src" src/Server/GameServer.java src/Common/*.java src/Model/*.java src/Server/*.java

$ sudo java -cp "bin:lib/mysql-connector-j-9.5.0.jar" Server.GameServer
```

成功后，终端提示：

```text
=== 语音服务器(UDP) 准备启动, 端口: 1234 ===
=== 语音服务器(UDP) 启动成功 ===
游戏服务器(TCP) 启动在端口: 81
```

### 2. 启动客户端

编译后直接运行即可。**如远端服务端未启动，会报错：java.net.ConnectException: 连接被拒绝**

```bash
$ javac -d bin -encoding UTF-8 -cp "lib/*:src" src/Chess.java

$ java -cp "bin:lib/*" Chess
```

### 3. 功能测试

* 运行两个客户端并注册登录后，会自动匹配。匹配后可正常对局。
* 聊天框内输入/ranking查看排行榜
* 聊天框内输入/history查看历史对局
* 麦克风和扬声器测试，如果在单一设备上测试，需要两个客户端选择不同的麦克风和扬声器设备，否则会产生占用问题。
* 对局结束后会自动退出登录
