package com.kii.beehive.portal.web.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by hdchen on 7/18/16.
 */
public class SubProtocolWebSocketHandler extends org.springframework.web.socket.messaging.SubProtocolWebSocketHandler {
	private ConcurrentWebSocketSessionHolder sessionHolder = ConcurrentWebSocketSessionHolder.getInstance();

	private static final Logger LOG = LoggerFactory.getLogger(SubProtocolWebSocketHandler.class);

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
		LOG.info("WebSocketSession id = " + session.getId() + ", thread id = " + Thread.currentThread().getId());
		sessionHolder.put(session.getId(), new WebSocketSessionDecorator(session));
		super.afterConnectionEstablished(sessionHolder.get(session.getId()));
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		LOG.info("WebSocketSession id = " + session.getId() + ", thread id = " + Thread.currentThread().getId()
				+ ", message = " + message.getPayload());
		super.handleMessage(sessionHolder.get(session.getId()), message);
	}

	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		LOG.info("message = " + message.getPayload());
		super.handleMessage(message);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		LOG.info("WebSocketSession id = " + session.getId() + ", thread id = " + Thread.currentThread().getId());
		super.handleTransportError(sessionHolder.get(session.getId()), exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		LOG.info("WebSocketSession id = " + session.getId() + ", thread id = " + Thread.currentThread().getId());
		super.afterConnectionClosed(sessionHolder.remove(session.getId()), closeStatus);
	}
}
