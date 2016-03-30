package com.kii.beehive.portal.web.help;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.AuthManager;
import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.store.entity.AuthInfoEntry;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.beehive.portal.web.constant.CallbackNames;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.exception.ObjectNotFoundException;

public class AuthInterceptor extends HandlerInterceptorAdapter {

    /**
     * @deprecated only for testing, so should not appear in any source code except for junit
     */
	private static final String USER_ID = "211102";

//	private  static final String AUTH_HEADER = "Authorization";


	private Logger log= LoggerFactory.getLogger(AuthInterceptor.class);

	@Value("${spring.profile}")
	private String  env;

	@Autowired
    private AuthManager authManager;

	@Autowired
	private OpLogTools logTool;


	@Autowired
	private DeviceSupplierDao supplierDao;

	@Autowired
	private AppInfoManager  appInfoManager;

	@Autowired
	private AppBindToolResolver  appInfoResolver;

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

		logRequest(request);

		// bypass the method OPTIONS
		if(Constants.HTTP_METHOD_OPTIONS.equalsIgnoreCase(request.getMethod())) {
			this.handleCORSMethodOptions(request, response, handler);
			return false;
		}

		// handle CORS request
		this.handleCORSMethodOthers(request, response, handler);


		String url=request.getRequestURI();

		String auth = request.getHeader(Constants.ACCESS_TOKEN);

		int idx=url.indexOf(Constants.URL_PREFIX);
		String subUrl=url.substring(idx+4).trim();

		List<String> list=new LinkedList<>();

		list.add(subUrl);
		list.add(auth);

		try {

			// below APIs don't need to check token and permission
			// - login: /oauth2/login
			// - register: /oauth2/register
			if(subUrl.startsWith(Constants.URL_OAUTH2_LOGIN) || subUrl.startsWith(Constants.URL_OAUTH2_REGISTER)){

				list.set(1,subUrl);
				logTool.write(list);

				return  super.preHandle(request, response, handler);

			}

			if (auth == null || !auth.startsWith(Constants.HEADER_BEARER)) {
				throw new BeehiveUnAuthorizedException(" auth token format invalid");
			}

			auth = auth.trim();

			String token = auth.substring(auth.indexOf(" ") + 1).trim();

			list.set(1,token);


			// TODO this checking is for testing only, must remove after testing complete
			if (Constants.SUPER_TOKEN.equals(token)&&(!"production".equals(env))) {

//				authManager.saveToken(USER_ID, token);

				AuthInfoStore.setAuthInfo(USER_ID);
				AuthInfoStore.setTeamID(null);
				list.set(1,USER_ID);
				logTool.write(list);

				return  super.preHandle(request, response, handler);
			}

			if(subUrl.startsWith(Constants.URL_USER)){
				//usersynccallback

				DeviceSupplier supplier= null;
				try {
					supplier = supplierDao.getSupplierByID(token);
				}catch(ObjectNotFoundException e) {
					log.debug(e.getMessage(), e);
					throw new BeehiveUnAuthorizedException(" DeviceSupplier Token invalid");
				}

				AuthInfoStore.setAuthInfo(supplier.getId());
			}else if(subUrl.startsWith(CallbackNames.CALLBACK_URL)){
				//trigger app callvback

				String appID=request.getHeader(Constants.HEADER_KII);

				if(!appInfoManager.verifyAppToken(appID,token)){
					throw new BeehiveUnAuthorizedException(" app callback unauthorized:app id "+appID);
				}
				AuthInfoStore.setAuthInfo(appID);

			}else {

				AuthInfoEntry authInfo = authManager.validateAndBindUserToken(token);
				log.debug(authInfo.toString());
				list.set(1, authInfo.getUserID());

				AuthInfoStore.setAuthInfo(authInfo.getUserID());
				AuthInfoStore.setTeamID(authInfo.getTeamID());
			}

			list.add("authSuccess");
		}catch(BeehiveUnAuthorizedException e){
			list.add("UnauthorizedAccess");
			logTool.write(list);
			throw e;
		}
		list.set(1,AuthInfoStore.getUserID());
		logTool.write(list);

		return super.preHandle(request, response, handler);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        authManager.unbindUserToken();
		AuthInfoStore.clear();
		appInfoResolver.clearAll();
		super.afterCompletion(request, response, handler, ex);
    }

	private void logRequest(HttpServletRequest request) {
		log.info("############### API Request ###############");

		log.info("# URI: " + request.getRequestURI());

		StringBuffer param = new StringBuffer();
		Enumeration<String> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			String value = request.getParameter(name);
			param.append(" <").append(name).append("=").append(value).append(">");
		}
		log.info("# URI Params: " + param.toString());

		StringBuffer header = new StringBuffer();
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
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
		response.addHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "origin, authorization, accept, content-type");
		response.addHeader("Access-Control-Max-Age", "99999");

		response.setContentType("application/jason");

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
