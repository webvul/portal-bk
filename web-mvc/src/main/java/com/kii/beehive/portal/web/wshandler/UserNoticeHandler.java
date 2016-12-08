package com.kii.beehive.portal.web.wshandler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.service.NoticeMsgQueue;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.entitys.AuthUser;
import com.kii.beehive.portal.manager.AuthManager;

@Component
public class UserNoticeHandler extends TextWebSocketHandler {
	
	
	
	@Autowired
	private ObjectMapper mapper;
	
	
	@Autowired
	private NoticeMsgQueue queue;
	
	
	@Autowired
	private AuthManager authManager;

	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
//		Long userID=599l;
//		AuthUser user= (AuthUser) ((Authentication)session.getPrincipal()).getDetails();
		
		String token =message.getPayload();
		
		AuthUser authInfo = authManager.validateLoginAccessToken(token);
		AuthInfoStore.setUserInfo(authInfo.getUser().getId());
		
		Long userID=authInfo.getUser().getId();
		
		queue.regist(userID, notice -> {
			
			if(notice==null){
				return true;
			}
			
			if(!session.isOpen()){
				return false;
			}
			
			try {
				notice.setReaded(null);
				
				TextMessage msg = new TextMessage(mapper.writeValueAsString(notice));
				
				session.sendMessage(msg);
				
				return true;
			}catch (JsonParseException e){
					return true;
			}catch(IOException ex){
				return false;
			}
		});
		

	}
	
}
