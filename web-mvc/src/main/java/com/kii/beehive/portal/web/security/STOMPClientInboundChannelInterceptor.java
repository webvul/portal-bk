//package com.kii.beehive.portal.web.security;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessagingException;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import com.kii.beehive.portal.auth.AuthInfoStore;
//import com.kii.beehive.portal.entitys.AuthUser;
//import com.kii.beehive.portal.web.socket.ConcurrentWebSocketSessionHolder;
//
///**
// * Created by hdchen on 7/13/16.
// */
//@Component
//public class STOMPClientInboundChannelInterceptor implements ChannelInterceptor {
//
//	private ConcurrentWebSocketSessionHolder sessionHolder = ConcurrentWebSocketSessionHolder.getInstance();
//
//
//	@Autowired
//	private AuthenticationFactory  factory;
//
//
//	@Override
//	public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//		StompHeaderAccessor headerAccessor =
//				MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//		if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
//			return message;
//		}
//		List<String> headers = headerAccessor.getNativeHeader(HttpHeaders.AUTHORIZATION);
//		if (headers == null) {
//			throw new MessagingException("Can't authenticate the current user. Missing authorization header");
//		}
//		try {
//			String token = headers.get(0);
//			Authentication authentication = factory.getAuthentication(token);
//
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//			sessionHolder.get(headerAccessor.getSessionId()).setPrincipal(authentication);
//			if (authentication instanceof AuthTokenAuthentication) {
//				AuthUser auth = (AuthUser) authentication.getDetails();
//				AuthInfoStore.setAuthInfo(auth.getUser().getId());
//				if (null != auth.getTeam()) {
//					AuthInfoStore.setTeamID(auth.getTeam().getId());
//				}
//			}
//			return message;
//		} catch (Exception e) {
//			throw new MessagingException("Can't authenticate the current user",
//					new BadCredentialsException(e.getMessage(), e));
//		}
//
//
//	}
//
//
//
//	@Override
//	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
//
//	}
//
//	@Override
//	public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
//
//	}
//
//	@Override
//	public boolean preReceive(MessageChannel channel) {
//		return true;
//	}
//
//	@Override
//	public Message<?> postReceive(Message<?> message, MessageChannel channel) {
//		return message;
//	}
//
//	@Override
//	public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
//
//	}
//}
