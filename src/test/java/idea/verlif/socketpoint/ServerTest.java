package idea.verlif.socketpoint;

import java.io.IOException;

public class ServerTest {

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
            } catch (IOException ignored) {
            }
        }).start();
    }
}
