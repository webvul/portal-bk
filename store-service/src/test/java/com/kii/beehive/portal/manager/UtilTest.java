package com.kii.beehive.portal.manager;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class UtilTest {

	@Test
	public void testStrRandom(){

		System.out.println( DigestUtils.sha1Hex("qwerty_user_id8ac08190-007e-11e6-9c6d-00163e007aba_beehive"));

	}
}
