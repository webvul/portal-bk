package com.kii.beehive.portal.web.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.SafeThreadTool;
import com.kii.beehive.portal.entitys.AuthInfo;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.web.constant.CallbackNames;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.beehive.portal.web.help.AuthUtils;
import com.kii.extension.sdk.context.AppBindToolResolver;

@Controller
public class AuthInterceptor extends HandlerInterceptorAdapter {

	private Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

	@Value("${spring.profile}")
	private String env;

	@Autowired
	private AuthManager authManager;
	
	@Autowired
	private DeviceSupplierDao supplierDao;
	
	@Autowired
	private AppInfoManager appInfoManager;
	
	@Autowired
	private AppBindToolResolver appInfoResolver;

	/**
	 * validate the token from header "Authorization"
	 * the token is assigned after login success
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


		if (Constants.HTTP_METHOD_OPTIONS.equalsIgnoreCase(request.getMethod())) {
			this.handleCORSMethodOptions(request, response, handler);
			return false;
		}

		// handle CORS request
		this.handleCORSMethodOthers(request, response, handler);


		String url = request.getRequestURI();

		int idx = url.indexOf(Constants.URL_PREFIX);
		String subUrl=url;
		if(idx!=-1) {
			subUrl = url.substring(idx + 4).trim();
		}

		if (subUrl.startsWith(Constants.URL_OAUTH2)
				|| subUrl.startsWith("/onboardinghelper")
				|| subUrl.contains("/debug/")
				|| subUrl.startsWith("/gatewayServer")) {


			return super.preHandle(request, response, handler);

		}

		String token = AuthUtils.getTokenFromHeader(request);

		if (StringUtils.isBlank(token)) {
			throw new PortalException(ErrorCode.INVALID_TOKEN);
		}

		try {

			if (subUrl.startsWith(CallbackNames.CALLBACK_URL)) {
				
					String appID = request.getHeader(Constants.HEADER_KII);
					
					if (!appInfoManager.verifyAppToken(appID, token)) {
						throw new PortalException(ErrorCode.INVALID_INPUT, "field", "appInfo", "data", appID);
					}
					AuthInfoStore.setAuthInfo(2L);
					
			} else if (subUrl.startsWith("/party3rd")) {
				

				//TODO:add verify to 3rdparty token.
				AuthInfoStore.setAuthInfo(3L);
			} else {


				AuthInfo authInfo = authManager.validateAndBindUserToken(token, request.getMethod(), subUrl);
				
				AuthInfoStore.setUserInfo(authInfo.getUserID());
				AuthInfoStore.setTeamID(authInfo.getTeamID());
				request.setAttribute("userIDStr", authInfo.getUserIDStr());
			}

		} catch (PortalException e) {
			throw e;
		}

		return super.preHandle(request, response, handler);

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

		SafeThreadTool.removeLocalInfo();
		super.afterCompletion(request, response, handler, ex);
	}

	private void logRequest(HttpServletRequest request) {
		log.info("############### API Request ###############");

		log.info("# URI: " + request.getRequestURI());

		StringBuffer param = new StringBuffer();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			String value = request.getParameter(name);
			param.append(" <").append(name).append("=").append(value).append(">");
		}
		log.info("# URI Params: " + param.toString());

		StringBuffer header = new StringBuffer();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			String value = request.getHeader(name);
			header.append(" <").append(name).append("=").append(value).append(">");
		}
		log.info("# Headers: " + header.toString());

		log.info("###########################################");
	}

	/**
	 * handle CORS request OPTIONS method
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws IOException
	 */
	private void handleCORSMethodOptions(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		// Add HTML5 CORS headers
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "origin, authorization, accept, content-type");
		response.addHeader("Access-Control-Max-Age", "86400");

//		response.setContentType("application/json");

		response.setStatus(200);
		response.getWriter().flush();

	}

	/**
	 * handle CORS request other methods
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws IOException
	 */
	private void handleCORSMethodOthers(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		// Add HTML5 CORS headers
		response.addHeader("Access-Control-Allow-Origin", "*");

	}
}
