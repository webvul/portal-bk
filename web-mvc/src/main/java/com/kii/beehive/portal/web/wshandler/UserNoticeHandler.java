package com.kii.beehive.portal.web.wshandler;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.service.NoticeMsgQueue;
import com.kii.beehive.portal.entitys.AuthUser;

@Component
public class UserNoticeHandler extends TextWebSocketHandler {
	
	
	
	@Autowired
	private ObjectMapper mapper;
	
	
	@Autowired
	private NoticeMsgQueue queue;

	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		AuthUser user= (AuthUser) ((Authentication)session.getPrincipal()).getDetails();
//
//
		Long userID=user.getUser().getId();

		
		queue.regist(userID, notice -> {
			
			
			if(!session.isOpen()){
				return false;
			}
			
			try {
				TextMessage msg = new TextMessage(mapper.writeValueAsString(notice));
				
				session.sendMessage(msg);
				
				return true;
			}catch (JsonParseException e){
					return true;
			}catch(IOException ex){
				return false;
			}
		});
		
//		executorService.scheduleAtFixedRate(() -> {
//
//			TextMessage msg=new TextMessage("user: time:"+System.currentTimeMillis()+ " req:"+message.getPayload());
//			try {
//				session.sendMessage(msg);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		},0, 1l,TimeUnit.MINUTES);
	}
	
	private ScheduledExecutorService  executorService=new ScheduledThreadPoolExecutor(10);
}
