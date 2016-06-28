package com.kii.beehive.portal.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.kii.beehive.portal.web.controller.STOMPMessageController;
import com.kii.beehive.portal.web.socket.EchoHandler;

/**
 * Created by hdchen on 6/27/16.
 */
@Configuration
@EnableWebSocket
@ComponentScan(basePackages = {"com.kii.beehive.portal.web.controller"}, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = STOMPMessageController.class)})
public class WebSocketConfig implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new EchoHandler(), "/echo").setAllowedOrigins("*");
	}
}