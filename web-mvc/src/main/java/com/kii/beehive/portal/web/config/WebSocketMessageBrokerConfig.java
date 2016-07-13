package com.kii.beehive.portal.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import com.kii.beehive.portal.web.security.STOMPClientInboundChannelInterceptor;

/**
 * Created by hdchen on 6/7/16.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
	@Autowired
	private STOMPClientInboundChannelInterceptor stompClientInboundChannelInterceptor;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stompSockJS").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/stomp").setAllowedOrigins("*");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}

	@Override
	protected void customizeClientInboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(stompClientInboundChannelInterceptor);
	}

	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
	}
}
