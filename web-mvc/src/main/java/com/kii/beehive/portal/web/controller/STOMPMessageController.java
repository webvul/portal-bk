package com.kii.beehive.portal.web.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.web.help.STOMPMessageQueue;
import com.kii.beehive.portal.web.help.ThingStatusInfo;

/**
 * Created by hdchen on 6/2/16.
 */
@RestController
public class STOMPMessageController {
	private STOMPMessageQueue stompMessageQueue;

	private SimpMessagingTemplate simpMessagingTemplate;

	private ExecutorService executorService;

	@Autowired
	public STOMPMessageController(STOMPMessageQueue stompMessageQueue, SimpMessagingTemplate simpMessagingTemplate) {
		this.stompMessageQueue = stompMessageQueue;
		this.simpMessagingTemplate = simpMessagingTemplate;
		executorService = Executors.newSingleThreadExecutor();
		executorService.submit(new Callable<Object>() {
			private ObjectMapper objectMapper = new ObjectMapper();

			@Override
			public Object call() throws Exception {
				while (true) {
					ThingStatusInfo info = STOMPMessageController.this.stompMessageQueue.pollThingStatus();
					try {
						STOMPMessageController.this.simpMessagingTemplate.convertAndSend("/topic/" + info.getAppId()
								+ "/" + info.getStatus().getThingID(), objectMapper.writeValueAsString(info.getStatus()));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@MessageMapping("/hello")
	@SendTo("/topic/operator")
	public String greeting(String message) throws Exception {
		return "Receive: " + message;
	}

	@RequestMapping(value = "/send/{message}")
	public String autoReply(@PathVariable("message") String message) {
		simpMessagingTemplate.convertAndSend("/topic/operator", message);
		return "Receive: " + message;
	}

	@RequestMapping(value = "/send/{message}/{topic}")
	public void sendMessageToTopic(@PathVariable("message") String message, @PathVariable("topic") String topic) {
		simpMessagingTemplate.convertAndSend("/topic/" + topic, message);
	}
}
