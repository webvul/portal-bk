package com.kii.beehive.portal.web.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.kii.beehive.portal.web.security.AuthenticationFactory;
import com.kii.beehive.portal.web.socket.EchoHandler;
import com.kii.beehive.portal.web.wshandler.UserNoticeHandler;

@Configuration
@EnableWebSocket
@Import(PropertySourcesPlaceholderConfig.class)
@ComponentScan(basePackages = {"com.kii.beehive.portal.web.security","com.kii.beehive.portal.web.wshandler"})
public class WSPullConfig implements WebSocketConfigurer {
	
	
	@Autowired
	private AuthenticationFactory factory;
	
	
	@Autowired
	private  UserNoticeHandler handler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry.addHandler(new EchoHandler(), "/echo").setAllowedOrigins("*");
		
		registry.addHandler(handler, "/users/notices")
//				.setHandshakeHandler(factory.getHandshakeHandler())
				.setAllowedOrigins("*");
	}
	
	
	
}
