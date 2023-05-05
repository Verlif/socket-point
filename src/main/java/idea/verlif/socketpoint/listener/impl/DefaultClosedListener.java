package idea.verlif.socketpoint.listener.impl;

import idea.verlif.socketpoint.EndPoint;
import idea.verlif.socketpoint.listener.ClosedListener;

/**
 * @author Verlif
 */
public class DefaultClosedListener implements ClosedListener {
    @Override
    public void onClosed(EndPoint endPoint) {
        // 关闭时不做操作
    }
}
