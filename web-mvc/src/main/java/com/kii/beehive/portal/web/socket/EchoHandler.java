package com.kii.beehive.portal.web.socket;

import java.io.IOException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by hdchen on 6/23/16.
 */
public class EchoHandler extends TextWebSocketHandler {
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		System.out.println(message.toString());
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
