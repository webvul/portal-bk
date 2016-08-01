package com.kii.beehive.portal.web.socket;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.web.security.AuthTokenAuthentication;
import com.kii.beehive.portal.web.stomp.StompDecoderDecorator;

/**
 * Created by hdchen on 7/18/16.
 */
public class SubProtocolWebSocketHandler extends org.springframework.web.socket.messaging.SubProtocolWebSocketHandler {
	private final StompDecoderDecorator stompDecoder;

	private ConcurrentWebSocketSessionHolder sessionHolder = ConcurrentWebSocketSessionHolder.getInstance();

	private static final Logger LOG = LoggerFactory.getLogger(SubProtocolWebSocketHandler.class);

	private static Field stompDecoderField;

	static {
		Field[] fields = StompSubProtocolHandler.class.getDeclaredFields();
		for (Field field : fields) {
			if (StompDecoder.class.isAssignableFrom(field.getType())) {
				stompDecoderField = field;
				stompDecoderField.setAccessible(true);
				try {
					Field modifiersField = Field.class.getDeclaredField("modifiers");
					modifiersField.setAccessible(true);
					modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Create a new {@code SubProtocolWebSocketHandler} for the given inbound and outbound channels.
	 *
	 * @param clientInboundChannel  the inbound {@code MessageChannel}
	 * @param clientOutboundChannel the outbound {@code MessageChannel}
	 * @param stompDecoderDecorator
	 */
	public SubProtocolWebSocketHandler(MessageChannel clientInboundChannel, SubscribableChannel clientOutboundChannel,
									   StompDecoderDecorator stompDecoderDecorator) {
		super(clientInboundChannel, clientOutboundChannel);
		stompDecoder = stompDecoderDecorator;
	}

	@Override
	public void setProtocolHandlers(List<SubProtocolHandler> protocolHandlers) {
		for (SubProtocolHandler handler : protocolHandlers) {
			replaceStompDecoder(handler);
		}
		super.setProtocolHandlers(protocolHandlers);
	}

	@Override
	public void addProtocolHandler(SubProtocolHandler handler) {
		replaceStompDecoder(handler);
		super.addProtocolHandler(handler);
	}

	@Override
	public void setDefaultProtocolHandler(SubProtocolHandler defaultProtocolHandler) {
		replaceStompDecoder(defaultProtocolHandler);
		super.setDefaultProtocolHandler(defaultProtocolHandler);
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
		final SecurityContext origin = SecurityContextHolder.getContext();
		final Long userId = AuthInfoStore.getUserID();
		final Long teamId = AuthInfoStore.getTeamID();
		try {
			WebSocketSessionDecorator wrapped = sessionHolder.get(session.getId());
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication((Authentication) wrapped.getPrincipal());
			SecurityContextHolder.setContext(context);
			if (wrapped.getPrincipal() instanceof AuthTokenAuthentication) {
				AuthRestBean auth = ((AuthTokenAuthentication) wrapped.getPrincipal()).getDetails();
				AuthInfoStore.setAuthInfo(auth.getUser().getId());
				AuthInfoStore.setTeamID(auth.getTeamID());
			}
			super.handleMessage(wrapped, message);
		} finally {
			AuthInfoStore.setAuthInfo(userId);
			AuthInfoStore.setTeamID(teamId);
			SecurityContextHolder.setContext(origin);
		}
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

	private void replaceStompDecoder(SubProtocolHandler handler) {
		if (handler instanceof StompSubProtocolHandler) {
			try {
				Object decoder = stompDecoderField.get(handler);
				if (decoder instanceof StompDecoderDecorator) {
					return;
				}
				stompDecoderField.set(handler, stompDecoder);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
