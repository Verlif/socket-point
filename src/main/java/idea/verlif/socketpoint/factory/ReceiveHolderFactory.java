package idea.verlif.socketpoint.factory;

import idea.verlif.socketpoint.EndPoint;
import idea.verlif.socketpoint.ReceiveHolder;

public interface ReceiveHolderFactory {

    /**
     * 创建信息接收处理器
     *
     * @param target 目标端点
     * @return 信息接收处理器对象
     */
    ReceiveHolder create(EndPoint target);
}
