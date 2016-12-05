package com.kii.beehive.portal.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.kii.beehive.portal.web.controller.STOMPMessageController;
import com.kii.beehive.portal.web.security.AuthenticationFactory;
import com.kii.beehive.portal.web.security.Role;
import com.kii.beehive.portal.web.stomp.MessageManager;

/**
 * Created by hdchen on 6/7/16.
 */
@Configuration
@EnableWebSocketMessageBroker
@Import({PropertySourcesPlaceholderConfig.class})
@ComponentScan(basePackages = {
		"com.kii.beehive.portal.web.controller",
		"com.kii.beehive.portal.web.stomp"
		},
		useDefaultFilters = false, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				value = {
						STOMPMessageController.class,
						MessageManager.class
				}
		)})
public class WebSocketMessageBrokerConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
//	@Autowired
//	private STOMPClientInboundChannelInterceptor stompClientInboundChannelInterceptor;

	@Value("${beehive.websocket.stomp.broker:/topic}")
	private String broker;

	@Value("${beehive.websocket.stomp.destination.prefix:/app}")
	private String destinationPrefix;

	@Value("${beehive.websocket.stomp.endpoint:/stomp}")
	private String enpointStomp;

	@Value("${beehive.websocket.stomp.endpoint.sockJS:/stompSockJS}")
	private String endporintStompSockJS;

	@Value("${beehive.websocket.stomp.allowedOrigins:*}")
	private String[] allowedOrigins;

	@Value("${beehive.websocket.stomp.sameOriginDisabled:true}")
	private boolean sameOriginDisabled;
	
	@Autowired
	private AuthenticationFactory factory;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		registry.addEndpoint(endporintStompSockJS).setHandshakeHandler(factory.getHandshakeHandler()).setAllowedOrigins(allowedOrigins).withSockJS();
		registry.addEndpoint(enpointStomp).setHandshakeHandler(factory.getHandshakeHandler()).setAllowedOrigins(allowedOrigins);

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
	

//	@Override
//	protected void customizeClientInboundChannel(ChannelRegistration registration) {
//		registration.setInterceptors(stompClientInboundChannelInterceptor);
//	}

	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages.simpTypeMatchers(SimpMessageType.CONNECT).permitAll()
				.simpTypeMatchers(SimpMessageType.MESSAGE).hasAnyAuthority(
				Role.administrator.name(), Role.userAdmin.name())
				.simpTypeMatchers(SimpMessageType.DISCONNECT).permitAll()
				.anyMessage().authenticated();
	}
	//ChannelInterceptorAdapter
}
