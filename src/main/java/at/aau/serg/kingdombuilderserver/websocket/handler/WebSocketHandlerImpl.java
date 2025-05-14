package at.aau.serg.kingdombuilderserver.websocket.handler;

import org.springframework.web.socket.*;

public class WebSocketHandlerImpl implements WebSocketHandler {


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // TODO handle the messages here
        session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        throw new UnsupportedOperationException("Default constructor is not supported.");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        throw new UnsupportedOperationException("Default constructor is not supported.");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
