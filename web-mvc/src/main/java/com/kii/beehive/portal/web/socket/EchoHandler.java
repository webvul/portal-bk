package com.kii.beehive.portal.web.socket;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by hdchen on 6/23/16.
 */
public class EchoHandler extends TextWebSocketHandler {
	
	private Logger log= LoggerFactory.getLogger(EchoHandler.class);
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		log.info(message.toString());
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
