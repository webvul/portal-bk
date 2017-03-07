package com.kii.beehive.portal.web.wshandler;


import java.io.IOException;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.sysmonitor.SysMonitorMsg;
import com.kii.beehive.portal.sysmonitor.SysMonitorQueue;


@Controller
public class SysNoticeHandler extends TextWebSocketHandler {
	
	
	@Autowired
	private ObjectMapper mapper;
	
	private Function<SysMonitorMsg, Boolean> callback;
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		
		SysMonitorQueue.getInstance().registerFire(session.getId(), notice -> {
			
			if (notice == null) {
				return true;
			}
			
			if (!session.isOpen()) {
				
				return false;
			}
			
			
			try {
				
				TextMessage msg = new TextMessage(mapper.writeValueAsString(notice));
				
				session.sendMessage(msg);
				
				return true;
			} catch (JsonParseException e) {
				return true;
			} catch (IOException ex) {
				return false;
			}
			
		});
		
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		SysMonitorQueue.getInstance().unRegisterFire(session.getId());
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		SysMonitorQueue.getInstance().unRegisterFire(session.getId());
		
	}
	
}
