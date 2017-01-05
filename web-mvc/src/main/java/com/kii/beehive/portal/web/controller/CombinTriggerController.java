package com.kii.beehive.portal.web.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.services.CombineTriggerService;
import com.kii.beehive.portal.store.entity.MLTriggerCombine;

@RestController
@RequestMapping(value = "/combineTrigger",consumes = {MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class CombinTriggerController {
	
	@Autowired
	private CombineTriggerService service;
	
	
	@RequestMapping(value = "/addTrigger",method  = {RequestMethod.POST},consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public String addTrigger(@RequestBody MLTriggerCombine trigger){
	
		return service.createTriggerWithML(trigger);
		
		
	}
	
	@RequestMapping(value = "/{triggerID}",method  = {RequestMethod.PUT},consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void updateTrigger(@RequestBody MLTriggerCombine trigger){
		
		service.updateMLTrigger(trigger);
	}
	
	@RequestMapping(value = "/{triggerID}",method  = {RequestMethod.DELETE})
	public void deleteTrigger(@PathVariable("triggerID") String triggerID){
	
		service.deleteTrigger(triggerID);
	}
	
	@RequestMapping(value = "/{triggerID}/enable",method  = {RequestMethod.PUT})
	public void enableTrigger(@PathVariable("triggerID") String triggerID){
		
		service.enableMLTrigger(triggerID);
	}
	
	@RequestMapping(value = "/{triggerID}/disable",method  = {RequestMethod.PUT})
	public void disableTrigger(@PathVariable("triggerID") String triggerID){
		
		service.disableMLTrigger(triggerID);
	}
	
	@RequestMapping(value = "/query/all",method  = {RequestMethod.GET})
	public List<MLTriggerCombine> getAllTrigger(){
		
		return service.getAll();
		
	}
	
	@RequestMapping(value = "/{triggerID}",method  = {RequestMethod.GET})
	public MLTriggerCombine getTrigger(@PathVariable("triggerID") String triggerID){
		
		
		return service.getTrigger(triggerID);
	}
}
