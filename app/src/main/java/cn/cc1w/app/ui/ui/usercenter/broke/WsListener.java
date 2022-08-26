package cn.cc1w.app.ui.ui.usercenter.broke;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.LogUtil;

/**
 * webSocket 监听器
 * @author kpinfo
 */
public class WsListener extends WebSocketAdapter {
    private static final String CHARSET = "UTF-8";
    private static final String ERROR_CONNECT = "connect_error";
    private static final String FAILURE_CONNECT = "connect_failure";
    private static final String SUCCESS_CONNECT = "connect_success";
    private static final String DATA_RECEIVE = "receive_data";

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);
    }

    /**
     * 接收到服务器传来的 二进制信息
     *
     * @param websocket webSocket
     * @param binary    接收到的二进制信息
     */
    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
        super.onBinaryMessage(websocket, binary);
        String message = new String(binary, CHARSET);
        EventBus.getDefault().post(new EventMessage(DATA_RECEIVE, message));
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
            throws Exception {
        super.onConnected(websocket, headers);
        EventBus.getDefault().post(new EventMessage(SUCCESS_CONNECT, SUCCESS_CONNECT));
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception)
            throws Exception {
        super.onConnectError(websocket, exception);
        LogUtil.e("连接错误  " + exception.getMessage());
        EventBus.getDefault().post(new EventMessage(ERROR_CONNECT, ERROR_CONNECT));
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
            throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
        EventBus.getDefault().post(new EventMessage(FAILURE_CONNECT, FAILURE_CONNECT));
    }
}