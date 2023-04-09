package idea.verlif.socketpoint.listener;

import idea.verlif.socketpoint.EndPoint;

public interface RejectedListener {

    /**
     * 拒绝端点连接的处理
     *
     * @param endPoint 被拒绝的端点对象，在这里允许对此端点发送信息
     */
    void onRejected(EndPoint endPoint);
}
