package com.kii.beehive.portal.web.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		String url = request.getRequestURI();

		int idx = url.indexOf(Constants.URL_PREFIX);
		String subUrl=url;
		if(idx!=-1) {
			subUrl = url.substring(idx + 4).trim();
		}

		if (subUrl.startsWith(Constants.URL_OAUTH2)
				|| subUrl.startsWith("/onboardinghelper")
				|| subUrl.contains("/demo/")
				|| subUrl.contains("/debug/")
				|| subUrl.startsWith("/gatewayServer")
				|| subUrl.startsWith("/party3rd")) {


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




}
