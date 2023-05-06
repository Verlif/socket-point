package idea.verlif.socketpoint;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class SimpleTest {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(16508));
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            printStream.println(123);
            printStream.println(SocketPoint.END_KEY);
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
        }
    }
}
