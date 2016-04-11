package com.kii.beehive.portal.web.help;

import javax.servlet.http.HttpServletRequest;

import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;

public class AuthUtils {


	public static String getTokenFromHeader(HttpServletRequest request){

		String auth = request.getHeader(Constants.ACCESS_TOKEN);


		if (auth == null || !auth.startsWith(Constants.HEADER_BEARER)) {
			throw new BeehiveUnAuthorizedException(" auth token format invalid");
		}

		auth = auth.trim();

		String token = auth.substring(auth.indexOf(" ") + 1).trim();

		return token;
	}
}
