package idea.verlif.socketpoint;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 * 端点
 */
public abstract class EndPoint implements Closeable {

    private final Socket target;

    public EndPoint(Socket target) {
        this.target = target;
    }

    public Socket getTarget() {
        return target;
    }

    /**
     * 向此端点发送信息
     *
     * @param message 向端点发送的信息
     */
    public abstract void send(String message);

    /**
     * 关闭与此端点的连接
     */
    @Override
    public void close() throws IOException {
        onClosed();
        target.close();
    }

    public boolean isConnected() {
        return target.isConnected();
    }

    public boolean isBound() {
        return target.isBound();
    }

    public boolean isClosed() {
        return target.isClosed();
    }

    public boolean isInputShutdown() {
        return target.isInputShutdown();
    }

    public boolean isOutputShutdown() {
        return target.isOutputShutdown();
    }

    protected abstract void onClosed();
}
