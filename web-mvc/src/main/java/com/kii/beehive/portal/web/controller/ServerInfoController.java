package com.kii.beehive.portal.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hdchen on 11/2/16.
 */
@RestController
@RequestMapping(value = "/info", consumes = {MediaType.ALL_VALUE}, produces = {MediaType.ALL_VALUE})
public class ServerInfoController {
	@RequestMapping(value = "/buildNum", method = {RequestMethod.GET})
	public String buildNumber() {
		return getClass().getPackage().getImplementationVersion();
	}
}
