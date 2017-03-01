package com.kii.beehive.portal.web.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.business.ruleengine.ExecuteCommandManager;
import com.kii.beehive.business.ruleengine.SecurityService;

@Controller
public class Security3PartyInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private SecurityService tool;
	
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private ExecuteCommandManager executeManager;
	
	
	
	
	private Pattern pattern = Pattern.compile("\\/party3rd\\/callback\\/([^\\/]+)");
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		
		String sign = request.getHeader("x-security-sign");
		
		String timeStamp = request.getHeader("x-security-timestamp");
		
		String context = StreamUtils.copyToString(request.getInputStream(), Charsets.UTF_8);
		
		String path = request.getRequestURI();
		
		Matcher match = pattern.matcher(path);
		
		if (!match.find()) {
			return false;
		}
		String groupName = match.group(1);
		
		
		if (!tool.verifySign(sign, context, groupName, Long.parseLong(timeStamp), request.getRequestURI())) {
			
			return false;
		}
		
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		
		Object obj = handlerMethod.getMethod().invoke(handlerMethod.getBean(), context);
		
		String resp = mapper.writeValueAsString(obj);
		
		StreamUtils.copy(resp, Charsets.UTF_8, response.getOutputStream());
		
		return false;
		
	}
	
	
}
