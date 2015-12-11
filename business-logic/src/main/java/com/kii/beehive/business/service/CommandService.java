package com.kii.beehive.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.entity.TagExpress;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.service.ThingIFService;

@Component
public class CommandService {


	@Autowired
	private ThingIFService  service;

	public void sendCmdToThing(String globalThingID,ThingCommand command){

	}

	public void sendCmdToTag(String  tagName,ThingCommand command){

	}

	public void sendCmdToTagExpress(TagExpress express,ThingCommand command){

	}
}
