package com.kii.beehive.business.trigger;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.BusinessTestTemplate;
import com.kii.beehive.business.ruleengine.ThingCommandForTriggerService;
import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;

public class TestCommand  extends BusinessTestTemplate {
	
	
	@Autowired
	private ObjectMapper mapper;
	
	 	@Autowired
	private ThingCommandForTriggerService commandService;
	 	
	 	
	 	
	 	@Test
	 	public void testThingCmd() throws IOException {
		   
			CommandToThing cmd= mapper.readValue(json,CommandToThing.class);
			
			
	 		commandService.executeCommand(cmd,new ExecuteParam());
	 		
	 		
		}
		
		
		private String json="{\n" +
				"      \"tagList\": [],\n" +
				"      \"thingList\": [\n" +
				"        \"1112\"\n" +
				"      ],\n" +
				"      \"doubleCheck\": false,\n" +
				"      \"type\": \"ThingCommand\",\n" +
				"      \"andExpress\": false,\n" +
				"      \"command\": {\n" +
				"        \"metadata\": {\n" +
				"          \"type\": \"Lighting\"\n" +
				"        },\n" +
				"        \"schemaVersion\": 0,\n" +
				"        \"actionResults\": [],\n" +
				"        \"actions\": [\n" +
				"          {\n" +
				"            \"turnPower\": {\n" +
				"              \"Power\": 1\n" +
				"            }\n" +
				"          }\n" +
				"        ]\n" +
				"      }\n" +
				"    }";
}
