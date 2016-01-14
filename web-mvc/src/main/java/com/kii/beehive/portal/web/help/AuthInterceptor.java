package com.kii.beehive.portal.web.help;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.store.entity.AuthInfoEntry;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.beehive.portal.web.constant.CallbackNames;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.extension.sdk.exception.UnauthorizedAccessException;

public class AuthInterceptor extends HandlerInterceptorAdapter {

    /**
     * @deprecated only for testing, so should not appear in any source code except for junit
     */
    public static final String SUPER_TOKEN = "super_token";

	private Logger log= LoggerFactory.getLogger(AuthInterceptor.class);


	@Autowired
    private AuthManager authManager;

	@Autowired
	private OpLogTools logTool;


	@Autowired
	private DeviceSupplierDao supplierDao;
    /**
     * validate the token from header "accessToken"
     * the token is assigned after login success
     *
     * // TODO need to discuss below:
     *  1. the token naming
     *  2. the compatibility with header Authorization
     *  3. the APIs to Interceptor (the setting in portalWebContext.xml)
     *  4. whether need to put token into UserTokenBindTool (ThreadLocal)
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		logRequest(request);

        String auth = request.getHeader(Constants.ACCESS_TOKEN);

		String url=request.getRequestURI();
		int idx=url.indexOf("/api/");
		String subUrl=url.substring(idx+4).trim();

		List<String> list=new LinkedList<>();
		list.add(subUrl);
		list.set(1,auth);

		try {

			if (auth == null || !auth.startsWith("Bearer ")) {
				throw new UnauthorizedAccessException();
			}

			auth = auth.trim();

			String token = auth.substring(auth.indexOf(" ") + 1).trim();

			// TODO this checking is for testing only, must remove after testing complete
			if (SUPER_TOKEN.equals(token)) {
				authManager.saveToken("211102", token);
			}

			if(subUrl.startsWith("/users")){
				//usersynccallback

				DeviceSupplier supper= supplierDao.getSupplierByID(token);
				if(supper==null){
					throw new UnauthorizedAccessException();
				}else{
					AuthInfoStore.setAuthInfo(supper.getId());
				}

			}else if(subUrl.startsWith(CallbackNames.CALLBACK_URL)){
				//trigger app callvback

			}else {

				AuthInfoEntry authInfo = authManager.validateAndBindUserToken(token);
				list.set(1, authInfo.getUserID());

				if (!authInfo.doValid(request.getRequestURI(), request.getMethod())) {
					throw new UnauthorizedAccessException();
				} else {
					AuthInfoStore.setAuthInfo(authInfo.getUserID());
				}
			}

			list.set(2,"loginSuccess");
		}catch(UnauthorizedAccessException e){
			list.set(2,"UnauthorizedAccess");
			logTool.write(list);
			throw e;
		}


		return super.preHandle(request, response, handler);


        
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        authManager.unbindUserToken();
		AuthInfoStore.clear();
		super.afterCompletion(request, response, handler, ex);
    }

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
