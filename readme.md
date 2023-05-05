# Socket Point

`socket-point`是 [socket-core](https://github.com/Verlif/socket-core) 的升级版，重构了代码，并且统一了监听逻辑。

这一次，你不再需要**client**与**server**了，这一次你需要的只是`SocketPoint`。

## 特点

- 无**client**与**server**的区别，统一为**SocketPoint**
- **SocketPoint**允许接收其他**SocketPoint**连接或是连接到其他的**SocketPoint**
- 同一个**SocketPoint**可以即是**server**也是**client**
- 允许自定义信息传输格式化，多行文本不再会被分成多条信息发送
- 拓展性更高的工厂模式与监听器，可以支持连接过滤、信息加密、限时连接等多种特性。

## 实例

```java
import idea.verlif.socketpoint.EndPointException;

public class MainTest {

    public static void main(String[] args) {
        // 开启端点服务器
        SocketPoint socketPoint = new SocketPoint();
        // 设置信息监听器
        socketPoint.setMessageListener((endPoint, message) -> {
            System.out.println(message);
            System.out.println("-----------------------------");
        });
        // 设置连接关闭监听器
        socketPoint.setClosedListener(endPoint ->
                System.out.println("连接被关闭 - " + endPoint.getTarget().getRemoteSocketAddress()));
        // 设置拒绝端点连接监听器，向来访端点发送拒绝信息
        socketPoint.setRejectedListener(endPoint ->
                endPoint.send("连接已达上限！ "));
        // 开启端点接收服务
        new Thread(() -> {
            try {
                socketPoint.start(new SocketConfig().max(2).tied(1));
            } catch (IOException e) {
                throw new EndPointException(e);
            }
        }).start();

        // 同时，此端点可以连接到另一个端点，这里是设置的本端点，默认的端口是16508
        SocketPoint.ConnectionHolder link = socketPoint.link("127.0.0.1", 16508);
        // 通过此连接进行信息交互
        link.send("你好呀！");
        // 一个端点可以连接无限个端点，并且共用之前设置的各种监听器
        SocketPoint.ConnectionHolder link2 = socketPoint.link("127.0.0.1", 16508);
        // 通过此连接进行信息交互
        link2.send("你好呀！");
    }
}
```

此时的控制台打印如下：

```text
你好呀！
-----------------------------
你好呀！
-----------------------------
```

## 添加依赖

### 添加Jitpack仓库源

last-version: [![Release](https://jitpack.io/v/Verlif/socket-point.svg)](https://jitpack.io/#Verlif/socket-point)

#### maven

```xml
<repositories>
   <repository>
       <id>jitpack.io</id>
       <url>https://jitpack.io</url>
   </repository>
</repositories>
```

#### Gradle

```text
allprojects {
  repositories {
      maven { url 'https://jitpack.io' }
  }
}
```

### 添加依赖

#### maven

```xml
<dependencies>
    <dependency>
        <groupId>com.github.Verlif</groupId>
        <artifactId>socket-point</artifactId>
        <version>last-version</version>
    </dependency>
</dependencies>
```

#### Gradle

```text
dependencies {
  implementation 'com.github.Verlif:socket-point:last-version'
}
```
