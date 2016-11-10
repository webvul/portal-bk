package com.kii.beehive.portal.web.help;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;
import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.portal.web.constant.Constants;

@Component
public class KiiDispatcherServlet extends DispatcherServlet {

	private OpLogTools logTool;

	private ApiLogUploadTools apiLogUploadTools;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		logTool = applicationContext.getBean(OpLogTools.class);
		apiLogUploadTools = applicationContext.getBean(ApiLogUploadTools.class);
	}

	public void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("doService:" + request.getRequestURI());
		super.doService(request, response);
		// api log
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


}