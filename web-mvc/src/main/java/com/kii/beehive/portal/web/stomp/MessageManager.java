package com.kii.beehive.portal.web.stomp;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.web.entity.StateUpload;
import com.kii.beehive.portal.web.help.InternalEventListenerRegistry;

/**
 * Created by hdchen on 7/11/16.
 */
@Component
public class MessageManager implements ApplicationListener,
		InternalEventListenerRegistry.ExtensionCallbackEventListener {
	@Autowired
	private InternalEventListenerRegistry internalEventListenerRegistry;


	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	private void init() {
		internalEventListenerRegistry.addEventListener(this);
	}

	@PreDestroy
	private void destroy() {
		internalEventListenerRegistry.removeEventListener(this);
	}

	@Async
	@Override
	public void onStateChange(String appId, StateUpload info) {
		try {
			simpMessagingTemplate.convertAndSend("/topic/" + appId
					+ "/" + info.getThingID(), objectMapper.writeValueAsString(info));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SessionSubscribeEvent) {
			SessionSubscribeEvent subscribeEvent = (SessionSubscribeEvent) event;
			System.out.println(subscribeEvent);
		} else if (event instanceof SessionUnsubscribeEvent) {
			SessionUnsubscribeEvent unsubscribeEvent = (SessionUnsubscribeEvent) event;
			System.out.println(unsubscribeEvent);
		}
	}
}
