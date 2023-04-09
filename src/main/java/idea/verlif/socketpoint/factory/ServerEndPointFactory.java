package idea.verlif.socketpoint.factory;

import idea.verlif.socketpoint.EndPoint;

import java.io.IOException;
import java.net.Socket;

public interface ServerEndPointFactory {

    /**
     * 创建服务端端点对象
     *
     * @param socket 服务端Socket对象
     * @return 创建的服务端端点对象
     */
    EndPoint create(Socket socket) throws IOException;
}
