package com.kii.beehive.obix.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.obix.service.LabbyService;
import com.kii.beehive.obix.web.entity.ObixContain;

@RestController
public class LabbyController {


	@Autowired
	private LabbyService  labby;



	@RequestMapping(path="/",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ObixContain getLabby(){
		return labby.getLabby();
	}

	@RequestMapping(path="/tags/haystack",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.TEXT_HTML_VALUE})
	public String getHayStackTags(){
		return labby.getHaystackTags();
	}


}
