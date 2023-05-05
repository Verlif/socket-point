package idea.verlif.socketpoint.listener.impl;

import idea.verlif.socketpoint.EndPoint;
import idea.verlif.socketpoint.listener.RejectedListener;

public class DefaultRejectedListener implements RejectedListener {
    @Override
    public void onRejected(EndPoint endPoint) {
        // 拒绝连接时不做处理
    }
}
