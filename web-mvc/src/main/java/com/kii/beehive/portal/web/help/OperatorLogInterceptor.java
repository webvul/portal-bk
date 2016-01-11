package com.kii.beehive.portal.web.help;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.constant.Constants;

public class OperatorLogInterceptor extends HandlerInterceptorAdapter {
	
//	private Logger operatorLog= LoggerFactory.getLogger(OperatorLogInterceptor.class);
//
	private Logger log= LoggerFactory.getLogger("com.kii");

	@Autowired
	private OpLogTools logTool;

	@Autowired
    private AuthManager authManager;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		this.logRequest(request);

		List<String> list=new ArrayList<>();

		list.add(String.valueOf(System.currentTimeMillis()));

		String auth = request.getHeader(Constants.ACCESS_TOKEN);

		if (auth != null && auth.startsWith("Bearer ")) {
			auth = auth.trim();

			String token = auth.substring(auth.lastIndexOf(" ") + 1).trim();
			AuthInfo authInfo = authManager.getAuthInfo(token);
			if(authInfo == null){
				list.add("");
			}else{
				list.add(authInfo.getUserID());
			}
			
		}
		list.add(request.getRequestURI());

		logTool.write(list);

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
