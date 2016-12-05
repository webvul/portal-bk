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
import com.kii.beehive.portal.web.socket.UserNoticeHandler;

/**
 * Created by hdchen on 6/27/16.
 */
@Configuration
@EnableWebSocket
//@ImportResource("classpath:com/kii/beehive/portal/web/portalContext.xml")
@Import({WebSocketMessageBrokerConfig.class, PropertySourcesPlaceholderConfig.class})
@ComponentScan(basePackages = {
		"com.kii.beehive.portal.web.socket"})
public class WebSocketConfig implements WebSocketConfigurer {
	
	
	@Autowired
	private AuthenticationFactory factory;
	
	@Autowired
	private UserNoticeHandler  handler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new EchoHandler(), "/echo").setAllowedOrigins("*");
		
		registry.addHandler(handler,"/users/notice")
//				.setHandshakeHandler(factory.getHandshakeHandler())
				.setAllowedOrigins("*");
	
		
	}
}
