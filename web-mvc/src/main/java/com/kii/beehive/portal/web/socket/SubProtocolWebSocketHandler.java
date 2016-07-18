package com.kii.beehive.portal.web.socket;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by hdchen on 7/18/16.
 */
public class SubProtocolWebSocketHandler extends org.springframework.web.socket.messaging.SubProtocolWebSocketHandler {
	private ConcurrentWebSocketSessionHolder sessionHolder = ConcurrentWebSocketSessionHolder.getInstance();

	/**
	 * Create a new {@code SubProtocolWebSocketHandler} for the given inbound and outbound channels.
	 *
	 * @param clientInboundChannel  the inbound {@code MessageChannel}
	 * @param clientOutboundChannel the outbound {@code MessageChannel}
	 */
	public SubProtocolWebSocketHandler(MessageChannel clientInboundChannel, SubscribableChannel clientOutboundChannel) {
		super(clientInboundChannel, clientOutboundChannel);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessionHolder.put(session.getId(), new WebSocketSessionDecorator(session));
		super.afterConnectionEstablished(sessionHolder.get(session.getId()));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessionHolder.remove(session.getId());
		super.afterConnectionClosed(session, closeStatus);
	}
}
