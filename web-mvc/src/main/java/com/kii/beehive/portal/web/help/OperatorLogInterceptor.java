package com.kii.beehive.portal.web.help;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class OperatorLogInterceptor extends HandlerInterceptorAdapter {
	
	private Logger operatorLog= LoggerFactory.getLogger(OperatorLogInterceptor.class);

	private Logger log= LoggerFactory.getLogger("com.kii");
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// output system log
		this.logRequest(request);

		// output operator log
		StringBuilder sb = new StringBuilder(100);
		String auth = request.getHeader("Authorization");

		if (auth != null && auth.startsWith("Bearer ")) {
			auth = auth.trim();
			String token = auth.substring(auth.lastIndexOf(" ") + 1).trim();
			sb.append(token);
		}
		sb.append(",").append(request.getRequestURI());
		operatorLog.info(sb.toString());
		return super.preHandle(request, response, handler);
	}

	/**
	 * log out uri, uri params and headers into system log
	 * @param request
     */
	private void logRequest(HttpServletRequest request) {
		log.info("*************** API Request ***************");
		log.info("URI: " + request.getRequestURI());

		StringBuffer param = new StringBuffer();
		Enumeration<String> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			String value = request.getParameter(name);
			param.append(" <").append(name).append("=").append(value).append(">");
		}
		log.info("URI Params: " + param.toString());

		StringBuffer header = new StringBuffer();
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			String value = request.getHeader(name);
			header.append(" <").append(name).append("=").append(value).append(">");
		}
		log.info("Headers: " + header.toString());

		log.info("*******************************************");
	}
}
