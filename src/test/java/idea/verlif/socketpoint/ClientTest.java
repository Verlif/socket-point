package idea.verlif.socketpoint;

import org.junit.Test;

import java.io.IOException;

public class ClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketPoint socketPoint = new SocketPoint();
        socketPoint.setMessageListener((endPoint, message) -> System.out.println(message));
        SocketPoint.ConnectionHolder link = socketPoint.link("127.0.0.1", 16508);
        link.send("你好！\n我呀！");
        Thread.sleep(1000);
        link.send("我好！");
        Thread.sleep(1000);
    }

    @Test
    public void multiClientTest() throws InterruptedException, IOException {
        Thread.sleep(1000);
        SocketPoint socketPoint = new SocketPoint();
        socketPoint.setMessageListener((endPoint, message) -> System.out.println(message));
        socketPoint.setConnectedListener(endPoint -> System.out.println("连接已关闭"));
        for (int i = 0; i < 20; i++) {
            SocketPoint.ConnectionHolder link = socketPoint.link("127.0.0.1", 16508);
            link.send("1");
        }
    }
}
