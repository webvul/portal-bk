package com.kii.beehive.portal.web.help;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import com.kii.beehive.portal.web.constant.Constants;

public class AuthUtils {


	public static String getTokenFromHeader(HttpServletRequest request){

		String auth = request.getHeader(Constants.ACCESS_TOKEN);

		if(StringUtils.isEmpty(auth)||!auth.startsWith(Constants.HEADER_BEARER)){
			return null;
		}

		auth = auth.trim();

		int idx=auth.indexOf(" ");
		if(idx==-1){
			return null;
		}
		String token = auth.substring( idx + 1).trim();

		return token;
	}
}
