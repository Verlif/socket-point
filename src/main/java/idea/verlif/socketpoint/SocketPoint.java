package idea.verlif.socketpoint;

import idea.verlif.socketpoint.factory.ClientEndPointFactory;
import idea.verlif.socketpoint.factory.ReceiveHolderFactory;
import idea.verlif.socketpoint.factory.ServerEndPointFactory;
import idea.verlif.socketpoint.listener.ClosedListener;
import idea.verlif.socketpoint.listener.ConnectedListener;
import idea.verlif.socketpoint.listener.MessageListener;
import idea.verlif.socketpoint.listener.RejectedListener;
import idea.verlif.socketpoint.listener.impl.DefaultClosedListener;
import idea.verlif.socketpoint.listener.impl.DefaultConnectedListener;
import idea.verlif.socketpoint.listener.impl.DefaultMessageListener;
import idea.verlif.socketpoint.listener.impl.DefaultRejectedListener;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketPoint {

    /**
     * 信息结束标记
     */
    public static final String END_KEY = "{[^END~-_";

    private SocketConfig config;

    private ConnectedListener connectedListener;
    private MessageListener messageListener;
    private ClosedListener closedListener;
    private RejectedListener rejectedListener;

    private ClientEndPointFactory clientEndPointFactory;
    private ServerEndPointFactory serverEndPointFactory;
    private ReceiveHolderFactory receiveHolderFactory;

    private ServerSocket server;
    private ServerState serverState;

    /**
     * 端点管理器列表
     */
    protected final List<EndPointHolder> endPointHolders;

    public SocketPoint() {
        endPointHolders = new ArrayList<>();
        serverState = ServerState.STOP;
    }

    /**
     * 开启端点服务器
     *
     * @param config 服务器配置
     */
    public void start(SocketConfig config) throws IOException {
        // 仅停止的端点可以开始运行
        if (ServerState.STOP == serverState) {
            serverState = ServerState.RUNNING;
            this.config = config;
            initListener();

            // 初始化加载
            endPointHolders.clear();
            for (int i = 0; i < config.getMax(); i++) {
                EndPointHolder holder = new EndPointHolder();
                endPointHolders.add(holder);
            }

            server = new ServerSocket(config.getPort());
            // 轮询连接
            while (!server.isClosed()) {
                Socket socket = server.accept();
                boolean add = false;
                EndPoint endPoint = clientEndPointFactory.create(socket);
                for (EndPointHolder holder : endPointHolders) {
                    EndPointHolder.EndPointHandler handler = holder.addEndPoint(endPoint);
                    if (handler != null) {
                        connectedListener.onConnected(endPoint);
                        add = true;
                        break;
                    }
                }
                if (!add) {
                    rejectedListener.onRejected(endPoint);
                    endPoint.close();
                }
            }
        }
    }

    /**
     * 停止端点服务器
     */
    public void stop() {
        if (server != null) {
            try {
                server.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 连接服务端
     *
     * @param ip   服务端IP地址
     * @param port 服务端端口
     */
    public ConnectionHolder link(String ip, Integer port) throws IOException {
        initListener();
        ConnectionHolder connectionHolder = new ConnectionHolder();
        connectionHolder.connect(ip, port);
        return connectionHolder;
    }

    private synchronized void initListener() {
        if (connectedListener == null) {
            connectedListener = new DefaultConnectedListener();
        }
        if (messageListener == null) {
            messageListener = new DefaultMessageListener();
        }
        if (closedListener == null) {
            closedListener = new DefaultClosedListener();
        }
        if (rejectedListener == null) {
            rejectedListener = new DefaultRejectedListener();
        }
        if (clientEndPointFactory == null) {
            clientEndPointFactory = new SpecialEndPointFactory();
        }
        if (serverEndPointFactory == null) {
            serverEndPointFactory = new SpecialEndPointFactory();
        }
        if (receiveHolderFactory == null) {
            receiveHolderFactory = new SpecialReceiveHolderFactory();
        }
    }

    public void setConnectedListener(ConnectedListener connectedListener) {
        this.connectedListener = connectedListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setClosedListener(ClosedListener closedListener) {
        this.closedListener = closedListener;
    }

    public void setRejectedListener(RejectedListener rejectedListener) {
        this.rejectedListener = rejectedListener;
    }

    public void setClientEndPointFactory(ClientEndPointFactory clientEndPointFactory) {
        this.clientEndPointFactory = clientEndPointFactory;
    }

    public void setServerEndPointFactory(ServerEndPointFactory serverEndPointFactory) {
        this.serverEndPointFactory = serverEndPointFactory;
    }

    public void setReceiveHolderFactory(ReceiveHolderFactory receiveHolderFactory) {
        this.receiveHolderFactory = receiveHolderFactory;
    }

    /**
     * 特殊接收处理器工厂类
     */
    public class SpecialReceiveHolderFactory implements ReceiveHolderFactory {

        @Override
        public ReceiveHolder create(EndPoint target) {
            return new SpecialReceiveHolder(target);
        }
    }

    /**
     * 特殊接收处理器
     */
    public final class SpecialReceiveHolder extends ReceiveHolder {

        private final StringBuilder stringBuilder;

        public SpecialReceiveHolder(EndPoint target) {
            super(target);
            this.stringBuilder = new StringBuilder();
        }

        @Override
        public void onClosed(EndPoint target) {
            closedListener.onClosed(target);
        }

        @Override
        public synchronized void receive(String message) {
            if (message.endsWith(END_KEY)) {
                stringBuilder.append(message, 0, message.length() - END_KEY.length());
                messageListener.receive(target, stringBuilder.toString());
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(message).append("\n");
            }
        }
    }

    /**
     * 特殊端点工厂类
     */
    public class SpecialEndPointFactory implements ClientEndPointFactory, ServerEndPointFactory {
        @Override
        public EndPoint create(Socket socket) throws IOException {
            return new SpecialEndPoint(socket);
        }
    }

    /**
     * 特殊端点
     */
    public class SpecialEndPoint extends EndPoint {

        private final PrintStream printStream;

        public SpecialEndPoint(Socket target) throws IOException {
            super(target);
            printStream = new PrintStream(target.getOutputStream());
        }

        @Override
        public void send(String message) {
            printStream.println(message + END_KEY);
            printStream.flush();
        }

        @Override
        protected void onClosed() {
            closedListener.onClosed(this);
        }

    }

    /**
     * 端点连接管理器
     */
    public class ConnectionHolder {

        private EndPoint target;

        public final ThreadPoolExecutor CLIENT_EXECUTOR = new ThreadPoolExecutor(
                1, 1,
                50, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    return thread;
                });

        private void connect(String ip, int port) throws IOException {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port));
            target = serverEndPointFactory.create(socket);

            ReceiveHolder handler = receiveHolderFactory.create(target);
            CLIENT_EXECUTOR.execute(handler);
            connectedListener.onConnected(target);
        }

        public void send(String message) {
            target.send(message);
        }

        public void close() {
            try {
                target.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 端点连接处理器
     *
     * @author Verlif
     */
    public class EndPointHolder {

        protected final ThreadPoolExecutor executor;

        /**
         * 当前管理器可容纳的端点连接最大值。
         */
        protected final int max;
        protected final List<EndPointHandler> endPointList;

        public EndPointHolder() {
            this.endPointList = new ArrayList<>();
            this.max = config.getMax();
            this.executor = new ThreadPoolExecutor(
                    (max / 2) + 1, max,
                    60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(8), (ThreadFactory) Thread::new);
        }

        /**
         * 添加新的连接端点
         *
         * @param endPoint 端点对象
         * @return 管理添加的端点的端点处理器，当此管理器无法添加新的端点时，返回null。
         */
        public synchronized EndPointHandler addEndPoint(EndPoint endPoint) {
            recycle();
            if (endPointList.size() >= max) {
                return null;
            }
            EndPointHandler handler;
            try {
                handler = new EndPointHandler(endPoint);
                endPointList.add(handler);
                executor.execute(handler);
                return handler;
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    endPoint.close();
                } catch (IOException ignored) {
                }
                return null;
            }
        }

        /**
         * 回收无用的端点连接
         */
        public synchronized void recycle() {
            List<EndPointHandler> list = new ArrayList<>(endPointList);
            for (EndPointHandler handler : list) {
                if (handler.getTarget() == null || handler.getTarget().isClosed()) {
                    endPointList.remove(handler);
                }
            }
        }

        /**
         * 关闭当前的管理器
         */
        public void stop() throws IOException {
            for (EndPointHandler handler : endPointList) {
                handler.close();
            }
        }

        public final class EndPointHandler implements Runnable {

            private final EndPoint target;
            private final PrintStream ps;
            private final ReceiveHolder holder;

            public EndPointHandler(EndPoint target) throws IOException {
                this.target = target;

                ps = new PrintStream(target.getTarget().getOutputStream());
                holder = receiveHolderFactory.create(target);
            }

            public void sendMessage(String message) {
                ps.println(message);
                ps.flush();
            }

            public void sendString(String str) {
                ps.print(str);
                ps.flush();
            }

            public void close() throws IOException {
                holder.close();
                ps.close();
                target.close();
            }

            public EndPoint getTarget() {
                return target;
            }

            @Override
            public void run() {
                holder.run();
            }
        }
    }

}
