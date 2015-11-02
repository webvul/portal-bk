package com.kii.beehive.portal.web.controller;


import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;

@RestController
@RequestMapping(path = "/tags",  consumes = {"application/json"}, produces = {"application/json"})
public class TagController {


	@RequestMapping(path = "/tag/{tagName}", method = {RequestMethod.GET})
	public List<GlobalThingInfo> getThingsByTag(@PathVariable("tagName") String tagName) {


		return null;

	}

	@RequestMapping(path = "/express/{tagExpress}", method = {RequestMethod.GET})
	public List<GlobalThingInfo> getThingByTagExpress(@PathVariable("tagName") String tagName){

		return null;

	}




}
