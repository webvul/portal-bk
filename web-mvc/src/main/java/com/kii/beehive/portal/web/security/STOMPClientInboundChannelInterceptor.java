package com.kii.beehive.portal.web.security;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.entitys.AuthUser;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.socket.ConcurrentWebSocketSessionHolder;

/**
 * Created by hdchen on 7/13/16.
 */
@Component
public class STOMPClientInboundChannelInterceptor implements ChannelInterceptor {
	@Autowired
	private AuthManager authManager;

	private ConcurrentWebSocketSessionHolder sessionHolder = ConcurrentWebSocketSessionHolder.getInstance();

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor headerAccessor =
				MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
			List<String> headers = Optional.ofNullable(
					headerAccessor.getNativeHeader(HttpHeaders.AUTHORIZATION)).orElse(Collections.emptyList());
			if (!headers.isEmpty()) {
				try {
					Authentication authentication;
					String token = headers.get(0);
					if (token.trim().toLowerCase().startsWith("bearer ")) {
						token = token.split(" ")[1];
						if (Constants.SUPER_TOKEN.equals(token)) {
							authentication = new SysAdminTokenAuthentication(token);
						} else {
							authentication =
									new AuthTokenAuthentication(authManager.validateLoginAccessToken(token));
						}
					} else if (token.trim().toLowerCase().startsWith("basic ")) {
						final String decodedBasicHeader = new String(DatatypeConverter.parseBase64Binary(
								token.split(" ")[1]), StandardCharsets.UTF_8);

						final int firstColonIndex = decodedBasicHeader.indexOf(':');

						String username = null;
						String password = null;

						if (firstColonIndex > 0) {
							username = decodedBasicHeader.substring(0, firstColonIndex);

							if (decodedBasicHeader.length() - 1 != firstColonIndex) {
								password = decodedBasicHeader.substring(firstColonIndex + 1);
							} else {
								//blank password
								password = "";
							}
						}
						authentication = new AuthTokenAuthentication(authManager.login(username, password));
					} else {
						throw new MessagingException("Can't authenticate the current user. Missing authorization header");
					}
					authentication.setAuthenticated(true);
					SecurityContextHolder.getContext().setAuthentication(authentication);
					sessionHolder.get(headerAccessor.getSessionId()).setPrincipal(authentication);
					if (authentication instanceof AuthTokenAuthentication) {
						AuthUser auth = (AuthUser) authentication.getDetails();
						AuthInfoStore.setAuthInfo(auth.getUser().getId());
						if (null != auth.getTeam()) {
							AuthInfoStore.setTeamID(auth.getTeam().getId());
						}
					}
					return message;
				} catch (Exception e) {
					throw new MessagingException("Can't authenticate the current user",
							new BadCredentialsException(e.getMessage(), e));
				}
			}

			throw new MessagingException("Can't authenticate the current user. Missing authorization header");
		}

		return message;
	}

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

	}

	@Override
	public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {

	}

	@Override
	public boolean preReceive(MessageChannel channel) {
		return true;
	}

	@Override
	public Message<?> postReceive(Message<?> message, MessageChannel channel) {
		return message;
	}

	@Override
	public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {

	}
}
