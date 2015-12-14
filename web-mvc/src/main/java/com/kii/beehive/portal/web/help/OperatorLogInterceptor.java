package com.kii.beehive.portal.web.help;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class OperatorLogInterceptor extends HandlerInterceptorAdapter {
	
	private Logger log= LoggerFactory.getLogger(OperatorLogInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		StringBuilder sb = new StringBuilder(100);
		String auth = request.getHeader("Authorization");

		if (auth != null && auth.startsWith("Bearer ")) {
			auth = auth.trim();
			String token = auth.substring(auth.lastIndexOf(" ") + 1).trim();
			sb.append(token);
		}
		sb.append(",").append(request.getRequestURI());
		log.info(sb.toString());
		return super.preHandle(request, response, handler);
	}
}
