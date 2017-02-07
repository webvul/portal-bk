package com.kii.beehive.portal.web.security;

import javax.xml.bind.DatatypeConverter;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.entitys.AuthUser;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.constant.Constants;

@Component
public class AuthenticationFactory {
	
	

	@Autowired
	private AuthManager authManager;
	
	private HandshakeHandler  handler=new DefaultHandshakeHandler() {
		@Override
		protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
			
			//Here you can set and return principal that is used by websocket session.
			List<String> auths = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
			if (auths == null || auths.isEmpty()) {
				throw new MessagingException("Can't authenticate the current user. Missing authorization header");
			}
			
			String auth = auths.get(0);
			Authentication authentication = getAuthentication(auth);
			
			if (authentication instanceof AuthTokenAuthentication) {
				AuthUser authUser = (AuthUser) authentication.getDetails();
				AuthInfoStore.setAuthInfo(authUser.getUser().getId());
				if (null != authUser.getTeam()) {
					AuthInfoStore.setTeamID(authUser.getTeam().getId());
				}
			}
			return authentication;
		}
	};
	
	public HandshakeHandler getHandshakeHandler() {
		
		return handler;
	}

	
	
	private Authentication getAuthentication(String token) {
		
		
		Authentication authentication;
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
		
		return authentication;
	}
}
