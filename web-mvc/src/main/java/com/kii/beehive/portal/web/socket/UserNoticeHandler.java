package com.kii.beehive.portal.web.socket;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.entitys.AuthUser;

@Component
public class UserNoticeHandler extends TextWebSocketHandler {
	
	
	
	@Autowired
	private ObjectMapper mapper;

	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		AuthUser user= (AuthUser) ((Authentication)session.getPrincipal()).getDetails();
		
		
		Long userID=user.getUser().getId();
		
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		executorService.scheduleAtFixedRate(() -> {
			
			TextMessage msg=new TextMessage("user:"+userID+" time:"+System.currentTimeMillis()+ " req:"+message.getPayload());
			try {
				session.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		},0, 1l,TimeUnit.MINUTES);
	}
	
	private ScheduledExecutorService  executorService=new ScheduledThreadPoolExecutor(10);
}
