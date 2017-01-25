package com.kii.beehive.portal.web.stomp;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.kii.beehive.portal.web.help.InternalEventListenerRegistry;

/**
 * Created by hdchen on 7/11/16.
 */
@Component
public class MessageManager implements ApplicationListener,
		InternalEventListenerRegistry.ExtensionCallbackEventListener {
	
	private Logger log= LoggerFactory.getLogger(MessageManager.class);
	
	@Autowired
	private InternalEventListenerRegistry internalEventListenerRegistry;


	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;



	@PostConstruct
	private void init() {
		internalEventListenerRegistry.addEventListener(this);
	}

	@PreDestroy
	private void destroy() {
		internalEventListenerRegistry.removeEventListener(this);
	}

	@Override
	public void onStateChange(String appId,long thingID, String info) {
			simpMessagingTemplate.convertAndSend("/topic/" + appId
					+ "/" + thingID, info);

	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SessionSubscribeEvent) {
			SessionSubscribeEvent subscribeEvent = (SessionSubscribeEvent) event;
			log.debug(subscribeEvent.toString());
		} else if (event instanceof SessionUnsubscribeEvent) {
			SessionUnsubscribeEvent unsubscribeEvent = (SessionUnsubscribeEvent) event;
			log.debug(unsubscribeEvent.toString());
		}
	}
}
