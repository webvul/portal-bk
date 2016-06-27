package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hdchen on 6/2/16.
 */
@RestController
public class STOMPMessageController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

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
}
