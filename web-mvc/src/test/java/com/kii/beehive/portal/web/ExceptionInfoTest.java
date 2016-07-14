package com.kii.beehive.portal.web;

import static junit.framework.TestCase.assertEquals;

import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.controller.ExceptionController;
import com.kii.beehive.portal.web.exception.PortalException;

public class ExceptionInfoTest extends WebTestTemplate{


	@Autowired
	private ExceptionController  controller;


	@Test
	public void testException(){

		controller.setLocale(Locale.ENGLISH);

		PortalException  excep=new PortalException(ErrorCode.INVALID_TOKEN);

		ResponseEntity<Object> resp=controller.handleStoreServiceException(excep);

		Map<String,Object> map= (Map<String, Object>) resp.getBody();


		assertEquals(map.get("errorCode"),ErrorCode.INVALID_TOKEN);

		assertEquals("invalid token",map.get("errorMessage"));

		assertEquals(resp.getStatusCode(),HttpStatus.UNAUTHORIZED);


	}

	@Test
	public void testExceptionWithTemplate(){

		controller.setLocale(Locale.ENGLISH);


		UnauthorizedException  excep=new UnauthorizedException(UnauthorizedException.LOGIN_TOKEN_INVALID,"token","abcdefg");


		ResponseEntity<Object> resp=controller.handleStoreServiceException(excep);

		Map<String,Object> map= (Map<String, Object>) resp.getBody();


		assertEquals(map.get("errorCode"),UnauthorizedException.LOGIN_TOKEN_INVALID);

		assertEquals("token token invalid",map.get("errorMessage"));

		assertEquals(resp.getStatusCode(),HttpStatus.UNAUTHORIZED);

	}


	@Test
	public void testi18n(){

		controller.setLocale(Locale.SIMPLIFIED_CHINESE);


		UnauthorizedException  excep=new UnauthorizedException(UnauthorizedException.LOGIN_TOKEN_INVALID,"token","1234567");



		ResponseEntity<Object> resp=controller.handleStoreServiceException(excep);

		Map<String,Object> map= (Map<String, Object>) resp.getBody();


		assertEquals(map.get("errorCode"),UnauthorizedException.LOGIN_TOKEN_INVALID);

		assertEquals("内容为 token 的令牌无效",map.get("errorMessage"));

		assertEquals(resp.getStatusCode(),HttpStatus.UNAUTHORIZED);

	}
}
