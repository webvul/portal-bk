package com.kii.beehive.portal.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/**", method = { RequestMethod.OPTIONS })
public class CORSController {

	@RequestMapping(path = "")
	public void doOptions(HttpServletRequest request, HttpServletResponse response) {

	}

}
