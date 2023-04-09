package idea.verlif.socketpoint;

/**
 * 套接字配置
 */
public class SocketConfig {

    /**
     * 套接字端口
     */
    protected int port = 16508;

    public SocketConfig port(int port) {
        this.port = port;
        return this;
    }

    /**
     * 最大连接处理器数量
     */
    protected int max = 2;

    /**
     * 设置最大连接处理器数量
     */
    public SocketConfig max(int max) {
        this.max = max;
        return this;
    }

    /**
     * 多少个连接共用一个连接处理器
     */
    protected int tied = 1;

    /**
     * 设置多少个连接共用一个连接处理器
     */
    public SocketConfig tied(int tied) {
        this.tied = tied;
        return this;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = Math.max(max, 1);
    }

    public int getTied() {
        return tied;
    }

    public void setTied(int tied) {
        this.tied = Math.max(tied, 1);
    }

}
