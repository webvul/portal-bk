package com.kii.beehive.portal.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.service.ThingMonitorService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.store.entity.ThingStatusMonitor;

@RestController
@RequestMapping(value = "/users/me/thingMonitors",consumes = {MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserMonitorController {
	
	
	@Autowired
	private ThingMonitorService  service;
	

	@RequestMapping(path="/addMonitor",method = {RequestMethod.POST})
	public void addMonitor(@RequestBody ThingStatusMonitor monitor){
		
		monitor.setCreator(AuthInfoStore.getUserID());
		
		service.addMonitor(monitor);
	}
	
	@RequestMapping(path="/{monitorID}",method = {RequestMethod.GET})
	public ThingStatusMonitor getMonitorByID(@PathVariable("monitorID")String monitorID){
		return service.getMonitor(monitorID);
	}
	
	@RequestMapping(path="/{monitorID}",method = {RequestMethod.PUT})
	public void updateMonitor(@PathVariable("monitorID")String monitorID,@RequestBody ThingStatusMonitor monitor){
		
		monitor.setId(monitorID);
		
		service.updateMonitor(monitor);
	}
	
	@RequestMapping(path="/{monitorID}",method = {RequestMethod.DELETE})
	public void deleteMonitor(@PathVariable("monitorID")String monitorID){
	
		service.removeMonitor(monitorID);
	}
	
	@RequestMapping(path="/name/{name}",method = {RequestMethod.GET})
	public List<ThingStatusMonitor> getMonitorByName(@PathVariable("name")String name){
	
		return null;
	}
	
	@RequestMapping(path="/{monitorID}/enable",method = {RequestMethod.PUT})
	public void enableMonitor(@PathVariable("monitorID")String monitorID){
		service.enableMonitor(monitorID);
	}
	
	@RequestMapping(path="/{monitorID}/disable",method = {RequestMethod.PUT})
	public void disableMonitor(@PathVariable("monitorID")String monitorID){
		service.disableMonitor(monitorID);
		
	}
}