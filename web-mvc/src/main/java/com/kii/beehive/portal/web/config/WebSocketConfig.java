package com.kii.beehive.portal.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;

import com.kii.beehive.portal.web.aop.WebSocketAspect;
import com.kii.beehive.portal.web.controller.STOMPMessageController;
import com.kii.beehive.portal.web.socket.EchoHandler;
import com.kii.beehive.portal.web.stomp.MessageManager;

/**
 * Created by hdchen on 6/27/16.
 */
@EnableWebMvc
@EnableWebSocket
@EnableWebSocketMessageBroker
@EnableAspectJAutoProxy
@Import({WebSocketMessageBrokerConfig.class, PropertySourcesPlaceholderConfig.class})
@ComponentScan(basePackages = {"com.kii.beehive.portal.web.controller", "com.kii.beehive.portal.web.stomp",
		"com.kii.beehive.portal.web.aop", "org.springframework.web.socket.config.annotation"},
		useDefaultFilters = false, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				value = {STOMPMessageController.class, MessageManager.class, WebSocketAspect.class,
						WebSocketMessageBrokerConfigurationSupport.class})})
public class WebSocketConfig implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new EchoHandler(), "/echo").setAllowedOrigins("*");
	}
}
