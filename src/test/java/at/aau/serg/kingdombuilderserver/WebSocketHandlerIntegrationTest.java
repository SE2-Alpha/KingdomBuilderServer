package at.aau.serg.kingdombuilderserver;

import at.aau.serg.kingdombuilderserver.websocket.WebSocketHandlerClientImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketHandlerIntegrationTest {

    @LocalServerPort
    private int port;

    private final String websocketUri = "ws://localhost:%d/websocket-example-handler";

    /**
     * Queue of messages from the server.
     */
    BlockingQueue<String> messages = new LinkedBlockingDeque<>();

    @Test
    void testWebSocketMessageBroker() throws Exception {
        WebSocketSession session = initStompSession();

        // send a message to the server
        String message = "Test message";
        session.sendMessage(new TextMessage(message));

        var expectedResponse = "echo from handler: " + message;
        assertThat(messages.poll(1, TimeUnit.SECONDS)).isEqualTo(expectedResponse);
    }

    /**
     * @return The basic session for the WebSocket connection.
     */
    public WebSocketSession initStompSession() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();

        // connect client to the websocket server
        return client.execute(new WebSocketHandlerClientImpl(messages), // pass the message list
                        String.format(websocketUri, port))
                // wait 1 sec for the client to be connected
                .get(1, TimeUnit.SECONDS);
    }

}
