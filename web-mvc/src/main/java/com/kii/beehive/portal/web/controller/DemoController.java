package com.kii.beehive.portal.web.controller;

import com.kii.beehive.portal.web.entity.HelloEntry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DemoController {


	@ResponseBody
	@RequestMapping(value = "/hello", method = {RequestMethod.GET}, consumes = {"*"})
	public ModelAndView hello() {

		HelloEntry entry = new HelloEntry();
		entry.setName("hello");
		entry.setValue("world");

		ModelAndView model = new ModelAndView();
		model.addObject(entry);
		model.setViewName("jsonView");

		return model;

	}

	@RequestMapping(value = "/echo", method = {RequestMethod.POST})
	public HelloEntry echo(@RequestBody HelloEntry entry) {

//		entry.setName("hello");
//		entry.setValue("world");

		return entry;

	}

}
