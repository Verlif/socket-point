package idea.verlif.socketpoint.factory;

import idea.verlif.socketpoint.EndPoint;

import java.io.IOException;
import java.net.Socket;

public interface ClientEndPointFactory {

    /**
     * 创建客户端端点对象
     *
     * @param socket 客户端Socket对象
     * @return 创建的客户端端点对象
     */
    EndPoint create(Socket socket) throws IOException;
}
