package com.kii.thingif.virtualthing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.industrytemplate.ActionInput;
import com.kii.beehive.industrytemplate.ThingSchema;
import com.kii.extension.sdk.entity.thingif.ActionResult;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class CommandOperate {

	@Autowired
	private ThingInfoStore store;

	@Autowired
	private ThingService service;

	@Async
	public void onCommandReceive(ThingCommand command, String thingID){

		ThingSchema schema=store.getInfo(thingID).getThingSchema();

		String appID=store.getInfo(thingID).getAppInfo();

		List<Map<String,ActionResult>> resultsList=new ArrayList<>();

		command.getActions().forEach((actMap)->{

			Map<String,ActionResult> results=new HashMap<>();
			actMap.forEach((name,action)->{

				ActionInput act= schema.getActions().get(name).getIn();

				Map<String,Object> map=action.getFields();

				map.keySet().retainAll(act.getProperties().keySet());

				ThingStatus newStatus=new ThingStatus();
				newStatus.setFields(map);

				service.setStatus(appID,thingID,newStatus);

				ActionResult result=new ActionResult();
				int  random= RandomUtils.nextInt(0,100);
				if(random<10){
					result.setSucceeded(false);
					result.setErrorMessage("error code:"+random);
				}else {
					result.setSucceeded(true);
				}
				results.put(name,result);
			});
			resultsList.add(results);
		});

		service.sendCommandResponse(appID,thingID,command.getCommandID(),resultsList);

	}
}
