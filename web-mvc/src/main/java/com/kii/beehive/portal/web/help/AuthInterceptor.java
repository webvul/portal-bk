package com.kii.beehive.portal.web.help;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kii.beehive.portal.util.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.extension.sdk.exception.UnauthorizedAccessException;

public class AuthInterceptor extends HandlerInterceptorAdapter {

    /**
     * @deprecated only for testing, so should not appear in any source code except for junit
     */
    public static final String SUPER_TOKEN = "super_token";

    @Autowired
    private AuthManager authManager;


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
        String auth = request.getHeader(Constants.ACCESS_TOKEN);

        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new UnauthorizedAccessException();
        }

        auth = auth.trim();

        String token = auth.substring(auth.indexOf(" ") + 1).trim();
       
        // TODO this checking is for testing only, must remove after testing complete
        if (SUPER_TOKEN.equals(token)) {
        	authManager.saveToken("211102", token);
            return true;
        }

        AuthInfo authInfo = authManager.validateAndBindUserToken(token);
        if (authInfo == null) {
            throw new UnauthorizedAccessException();
        }
		AuthInfoStore.setAuthInfo(authInfo);
        
        //check Auth
        String url = request.getMethod()+" "+request.getRequestURI().replaceAll(Constants.DOMAIN_URL, "");
        PathMatcher pathMatcher = new AntPathMatcher();
        for(String action:authInfo.getPermisssionSet()){
        	if(pathMatcher.match(action, url)){
        		return super.preHandle(request, response, handler);
        	}
        }
        
        //no permission
        throw new UnauthorizedAccessException();
        
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        authManager.unbindUserToken();

		AuthInfoStore.clear();
		super.afterCompletion(request, response, handler, ex);
    }
}
