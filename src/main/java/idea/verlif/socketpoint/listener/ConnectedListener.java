package idea.verlif.socketpoint.listener;

import idea.verlif.socketpoint.EndPoint;

/**
 * 当Socket连接上时回调接口
 *
 * @author Verlif
 */
public interface ConnectedListener {

    /**
     * 当连接创建时
     *
     * @param endPoint 连接的端点对象
     */
    void onConnected(EndPoint endPoint);

}
