package idea.verlif.socketpoint;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 信息接收处理器
 *
 * @author Verlif
 * @version 1.0
 */
public abstract class ReceiveHolder implements Runnable {

    protected final EndPoint target;
    protected boolean available;

    public ReceiveHolder(EndPoint target) {
        this.target = target;
        this.available = true;
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(target.getTarget().getInputStream())) {
            while (available && !target.getTarget().isClosed()) {
                receive(scanner.nextLine());
            }
        } catch (NoSuchElementException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        onClosed(target);
    }

    public void close() throws IOException {
        available = false;
        target.close();
    }

    /**
     * 当连接关闭时回调
     */
    protected void onClosed(EndPoint endPoint) {
    }

    /**
     * 当接收到数据时回调
     *
     * @param message 接收到的数据
     */
    public abstract void receive(String message);
}
