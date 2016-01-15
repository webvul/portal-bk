package com.kii.beehive.portal.web.exception;

import org.springframework.http.HttpStatus;

import com.kii.beehive.portal.web.constant.ErrorCode;

public class BeehiveUnAuthorizedException extends PortalException{


	private static final long serialVersionUID = 6341018761603394787L;

	public BeehiveUnAuthorizedException(String msg){

		super(ErrorCode.AUTH_FAIL,msg, HttpStatus.UNAUTHORIZED);
	}

}
