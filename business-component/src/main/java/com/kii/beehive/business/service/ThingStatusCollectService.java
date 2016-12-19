package com.kii.beehive.business.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.elasticsearch.ESService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

@Component
public class ThingStatusCollectService {
	
	
	@Autowired
	private ESService service;
	
	
	@Autowired
	private ThingTagManager thingManager;
	
	
	public void addThing(GlobalThingInfo thing){
	
		
		
		
	}
	
	public void fillThing(){
		
		thingManager.getAllThing().forEach((th)->{
			addThing(thing);
		})
		
	}
}
