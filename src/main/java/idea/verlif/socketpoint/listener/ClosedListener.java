package idea.verlif.socketpoint.listener;

import idea.verlif.socketpoint.EndPoint;

/**
 * 端点断开连接回调接口
 *
 * @author Verlif
 */
public interface ClosedListener {

    /**
     * 当连接断开是回调
     *
     * @param endPoint 断开的端点对象
     */
    void onClosed(EndPoint endPoint);
}
