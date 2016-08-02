package com.kii.beehive.portal.web.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.web.entity.HelloEntry;

@RestController
@RequestMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DemoController {


//	@ResponseBody
	@RequestMapping(value = "/hello", method = {RequestMethod.GET}, consumes = {"*"})
	public Map<String,Object> hello() {

		HelloEntry entry = new HelloEntry();
		entry.setName("hello");
		entry.setValue("world");

//		ModelAndView model = new ModelAndView();
//		model.addObject(entry);
//		model.setViewName("jsonView");

		CustomData data=new CustomData();

		return data.getData();

	}

	@RequestMapping(value = "/echo", method = {RequestMethod.POST})
	public HelloEntry echo(@RequestBody HelloEntry entry) {

//		entry.setName("hello");
//		entry.setValue("world");

		return entry;

	}

}
