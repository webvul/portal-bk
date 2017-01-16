package com.kii.beehive.portal.web.help;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.portal.web.constant.Constants;

/**
 * Created by carlos.yang on 16/12/15.
 */
public class LogFilter implements Filter {

	private OpLogTools logTool;

	private ApiLogUploadTools apiLogUploadTools;

	@Override
	public void init(FilterConfig config) throws ServletException {
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		logTool = applicationContext.getBean(OpLogTools.class);
		apiLogUploadTools = applicationContext.getBean(ApiLogUploadTools.class);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		filterChain.doFilter(servletRequest, servletResponse);
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		apiLog(request, response);
	}

	private void apiLog(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getRequestURI();
		int idx = url.indexOf(Constants.URL_PREFIX);
		String subUrl = url.substring(idx + 4).trim().replaceAll(",","`");
		Object userIDStrObject = request.getAttribute("userIDStr");
		String userIDStr = userIDStrObject == null ? "" : (String)userIDStrObject;
		List<String> list = new LinkedList<>();
		list.add(subUrl);
		list.add(request.getMethod());
		list.add(String.valueOf(response.getStatus()));
		list.add(userIDStr);
		list.add(request.getHeader(Constants.ACCESS_TOKEN));
		logTool.write(list);

		// upload api log to ES
		apiLogUploadTools.upload(url.substring(idx + 4).trim(), request.getMethod(), response.getStatus(),
				userIDStr, request.getHeader(Constants.ACCESS_TOKEN));

	}

	@Override
	public void destroy() {

	}
}
