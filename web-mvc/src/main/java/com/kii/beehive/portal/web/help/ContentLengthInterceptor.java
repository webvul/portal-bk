package com.kii.beehive.portal.web.help;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class ContentLengthInterceptor extends HandlerInterceptorAdapter {


	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {


//		int contentLength=((ResponseFacade) response).getContentWritten();
//
//		response.setContentLength(contentLength);

		super.afterCompletion(request, response, handler, ex);
	}


}
