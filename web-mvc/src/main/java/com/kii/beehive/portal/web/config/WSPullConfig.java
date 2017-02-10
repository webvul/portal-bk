package com.kii.beehive.portal.web.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.kii.beehive.portal.web.socket.EchoHandler;
import com.kii.beehive.portal.web.wshandler.SysNoticeHandler;
import com.kii.beehive.portal.web.wshandler.UserNoticeHandler;

@EnableWebMvc
@EnableWebSocket
@ComponentScan(basePackages = {"com.kii.beehive.portal.web.wshandler"},
		includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})})
public class WSPullConfig implements WebSocketConfigurer {
	
	

	@Autowired
	private  UserNoticeHandler handler;
	
	
	@Autowired
	private SysNoticeHandler sysNoticeHandler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry.addHandler(new EchoHandler(), "/echo").setAllowedOrigins("*");
		
		registry.addHandler(handler, "/users/notices")
				.addHandler(sysNoticeHandler,"/sys/monitor")
				.setAllowedOrigins("*");
	}
	
	
	
}
