package com.kii.beehive.portal.web.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.web.entity.HelloEntry;

@RestController
public class DemoController {


	@RequestMapping(path="/hello",method={RequestMethod.GET},consumes = {"application/json"},produces = {"application/json"})
	public HelloEntry hello(){

		HelloEntry entry=new HelloEntry();
		entry.setName("hello");
		entry.setValue("world");

		return entry;

	}

	@RequestMapping(path="/echo",method={RequestMethod.POST},consumes = {"application/json"},produces = {"application/json"})
	public HelloEntry echo(@RequestBody HelloEntry entry){

//		entry.setName("hello");
//		entry.setValue("world");

		return entry;

	}

}
