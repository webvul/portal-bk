package com.kii.beehive.portal.web.help;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kii.beehive.portal.helper.PortalTokenService;
import com.kii.extension.sdk.exception.UnauthorizedAccessException;

public class TokenInterceptor extends HandlerInterceptorAdapter {


	@Autowired
	private PortalTokenService tokenService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String path=request.getContextPath();

		if(!path.startsWith("users")){
			return true;
		}

		String auth=request.getHeader("Authorization");

		if(auth==null||!auth.startsWith("Bearer ")){

			throw new UnauthorizedAccessException();
		}

		String token=auth.substring(7);


		tokenService.setToken(token, PortalTokenService.PortalTokenType.UserSync);

		return true;

	}
}
