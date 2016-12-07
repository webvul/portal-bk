package com.kii.beehive.portal.web.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.service.ThingMonitorService;
import com.kii.beehive.portal.service.ThingStatusMonitorDao;
import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.beehive.portal.web.entity.ThingMonitorInput;
import com.kii.extension.sdk.service.AbstractDataAccess;

@RestController
@RequestMapping(value = "/users/me/thingMonitors",consumes = {MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserMonitorController {
	
	
	@Autowired
	private ThingMonitorService  service;
	

	@RequestMapping(path="/addMonitor",method = {RequestMethod.POST},consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public Map<String,String> addMonitor(@RequestBody ThingMonitorInput monitor){
		
		String id= service.addMonitor(getInputEntity(monitor));
		
		return Collections.singletonMap("monitorID",id);
	}
	
	@RequestMapping(path="/{monitorID}",method = {RequestMethod.GET})
	public ThingMonitorInput getMonitorByID(@PathVariable("monitorID")String monitorID){
		return getOutputView(service.getMonitor(monitorID));
	}
	
	@RequestMapping(path="/{monitorID}",method = {RequestMethod.PUT},consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void updateMonitor(@PathVariable("monitorID")String monitorID,@RequestBody ThingMonitorInput input){
		
		ThingStatusMonitor monitor=getInputEntity(input);
		
		monitor.setId(monitorID);
		
		service.updateMonitor(monitor);
	}
	
	@RequestMapping(path="/{monitorID}",method = {RequestMethod.DELETE})
	public void deleteMonitor(@PathVariable("monitorID")String monitorID){
	
		service.removeMonitor(monitorID);
	}
	
	@RequestMapping(path="/name/{name}",method = {RequestMethod.GET})
	public ThingMonitorInput getMonitorByName(@PathVariable("name")String name){
		
		
		ThingStatusMonitorDao.MonitorQuery query=new ThingStatusMonitorDao.MonitorQuery();
		query.setName(name);
		
		List<ThingStatusMonitor> list=service.queryMonitor(query,null);
		
		if(list.isEmpty()){
			return null;
		}else {
			return getOutputView(list.get(0));
		}
	}
	
	@RequestMapping(path="/query",method = {RequestMethod.POST},consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public List<ThingMonitorInput> queryMonitor(@RequestBody ThingStatusMonitorDao.MonitorQuery query,@RequestParam(name="pager",required=false)String sign){
	
		
		return service.queryMonitor(query, AbstractDataAccess.KiiBucketPager.getInstance(sign)).stream().map(UserMonitorController::getOutputView).collect(Collectors.toList());
	}
	

	
	@RequestMapping(path="/{monitorID}/enable",method = {RequestMethod.PUT})
	public void enableMonitor(@PathVariable("monitorID")String monitorID){
		service.enableMonitor(monitorID);
	}
	
	
	@RequestMapping(path="/{monitorID}/disable",method = {RequestMethod.PUT})
	public void disableMonitor(@PathVariable("monitorID")String monitorID){
		service.disableMonitor(monitorID);
	}
	
	private static ThingMonitorInput getOutputView(ThingStatusMonitor monitor){
		
		if(monitor==null){
			return null;
		}
		ThingMonitorInput view=new ThingMonitorInput();
		BeanUtils.copyProperties(monitor,view);
		
		view.setThings(new HashSet<>(monitor.getVendorThingIDList()));
		view.setMonitorID(monitor.getId());
		view.setEnable(monitor.getStatus()== ThingStatusMonitor.MonitorStatus.enable);
		
		return view;
	}
	
	
//
	private static ThingStatusMonitor getInputEntity(ThingMonitorInput input){
		
		ThingStatusMonitor entity=new ThingStatusMonitor();
		
		BeanUtils.copyProperties(input,entity);
		
		entity.setVendorThingIDList(input.getThings());
		
		return entity;
		
	}
}