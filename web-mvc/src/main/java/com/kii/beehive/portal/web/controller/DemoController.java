package com.kii.beehive.portal.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.kii.beehive.portal.web.entity.HelloEntry;

@RestController
@RequestMapping(path="/",consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DemoController {


	@ResponseBody
	@RequestMapping(path="/hello",method={RequestMethod.GET}, consumes = {"*"})
	public ModelAndView hello(){

		HelloEntry entry=new HelloEntry();
		entry.setName("hello");
		entry.setValue("world");

		ModelAndView model=new ModelAndView();
		model.addObject(entry);
		model.setViewName("jsonView");

		return model;

	}

	@RequestMapping(path="/echo",method={RequestMethod.POST})
	public HelloEntry echo(@RequestBody HelloEntry entry){

//		entry.setName("hello");
//		entry.setValue("world");

		return entry;

	}

}
