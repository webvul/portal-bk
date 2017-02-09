package com.kii.beehive.portal.web.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.services.MLTaskService;
import com.kii.beehive.portal.store.entity.MLTaskDetail;

@RestController
@RequestMapping(value = "/mlTasks",consumes = {MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class MLTaskController {
	
	
	@Autowired
	private MLTaskService service;
	
	@RequestMapping(value = "/{taskID}", method = {RequestMethod.PUT}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void addMLTask(@PathVariable("taskID") String taskID, @RequestBody MLTaskDetail detail) {
		
		detail.setMlTaskID(taskID);
		service.updateTask(taskID, detail);
		
	}
	
	@RequestMapping(value = "/{taskID}", method = {RequestMethod.GET})
	public MLTaskDetail getMLTask(@PathVariable("taskID") String taskID) {
		
		return service.getTaskDetailByID(taskID);
		
	}
	
	@RequestMapping(value = "/{taskID}", method = {RequestMethod.DELETE})
	public void removeMLTask(@PathVariable("taskID") String taskID) {
		
		service.removeTask(taskID);
		
	}
	
	@RequestMapping(value = "/{taskID}/enable", method = {RequestMethod.PUT})
	public void enableMLTask(@PathVariable("taskID") String taskID) {
		
		service.setEnable(taskID);
	}
	
	@RequestMapping(value = "/{taskID}/disable", method = {RequestMethod.PUT})
	public void disableMLTask(@PathVariable("taskID") String taskID) {
		
		service.setDisable(taskID);
	}
	
	@RequestMapping(value = "/query/all", method = {RequestMethod.GET})
	public List<MLTaskDetail> getAll() {
		
		return service.getAll();
	}
	
	@RequestMapping(value = "/task/demo/{taskID}",method  = {RequestMethod.GET})
	public Map<String,Object> getDemoValue(@PathVariable("taskID") String taskID){
		
		
		int seed= (int) (System.currentTimeMillis()%10  - 5);
		Map<String,Object> demo=new HashMap<>();
		
		demo.put("foo",3*seed);
		demo.put("bar",-7*seed);
		demo.put("taskID",taskID);
		return  demo;
	}
}

