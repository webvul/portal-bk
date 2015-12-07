package com.kii.extension.sdk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.thingif.CommandDetail;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.impl.KiiCloudClient;

public class ThingIFService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;


	@Autowired
	private TokenBindToolResolver tool;


	public String sendCommand(String thingID,ThingCommand command){
		/*
		http://api.kii.com/thing-if/apps/aa407bbe/targets/THING:th.75026fa00022-0819-5e11-b899-01595ce7/commands
		 */

		return null;
	}

	public CommandDetail readCommand(String thingID, String commandID){

		return null;
	}

	public List<CommandDetail> queryCommand(String thingID, int bestLimit, String... nextPaginationKey){

		return null;

	}

	public ThingStatus getStatus(String thingID){


		return null;
	}

	public void registTrigger(String thingID,ThingTrigger trigger){


	}
}
