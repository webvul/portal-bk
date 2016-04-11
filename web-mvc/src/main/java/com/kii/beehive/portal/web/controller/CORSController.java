package com.kii.beehive.portal.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(value = "/**", method = {RequestMethod.OPTIONS})
public class CORSController {

	@RequestMapping(value = "")
	public void doOptions(HttpServletRequest request, HttpServletResponse response) {

	}

}
