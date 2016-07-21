package com.kii.beehive.portal.helper;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.kii.beehive.portal.exception.UnauthorizedException;

public class TestException {

	@Test
	public void testException(){

		UnauthorizedException excep=new UnauthorizedException("mock","foo","bar");

		Map<String,String> params=excep.getParamMap();

		assertEquals("bar",params.get("foo"));

	}
}
