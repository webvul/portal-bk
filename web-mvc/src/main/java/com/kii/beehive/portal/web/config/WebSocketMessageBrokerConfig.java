package com.kii.beehive.portal.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import com.kii.beehive.portal.web.security.Role;
import com.kii.beehive.portal.web.security.STOMPClientInboundChannelInterceptor;

/**
 * Created by hdchen on 6/7/16.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
	@Autowired
	private STOMPClientInboundChannelInterceptor stompClientInboundChannelInterceptor;

	@Value("${beehive.websocket.stomp.broker}")
	private String broker;

	@Value("${beehive.websocket.stomp.destination.prefix}")
	private String destinationPrefix;

	@Value("${beehive.websocket.stomp.endpoint}")
	private String enpointStomp;

	@Value("${beehive.websocket.stomp.endpoint.sockJS}")
	private String endporintStompSockJS;

	@Value("${beehive.websocket.stomp.allowedOrigins}")
	private String[] allowedOrigins;

	@Value("${beehive.websocket.stomp.sameOriginDisabled}")
	private boolean sameOriginDisabled;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(endporintStompSockJS).setAllowedOrigins(allowedOrigins).withSockJS();
		registry.addEndpoint(enpointStomp).setAllowedOrigins(allowedOrigins);
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker(broker);
		config.setApplicationDestinationPrefixes(destinationPrefix);
	}

	@Override
	protected boolean sameOriginDisabled() {
		return sameOriginDisabled;
	}

	@Override
	protected void customizeClientInboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(stompClientInboundChannelInterceptor);
	}

	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages.simpTypeMatchers(SimpMessageType.CONNECT).permitAll()
				.simpTypeMatchers(SimpMessageType.MESSAGE).hasAnyAuthority(
				Role.administrator.name(), Role.userAdmin.name())
				.anyMessage().authenticated();
	}
}
