package idea.verlif.socketpoint.listener;

import idea.verlif.socketpoint.EndPoint;

/**
 * 接收服务端消息接口
 *
 * @author Verlif
 */
public interface MessageListener {

    /**
     * 当接收到数据时回调
     *
     * @param endPoint 端点对象
     * @param message  接收到的数据
     */
    void receive(EndPoint endPoint, String message);
}
