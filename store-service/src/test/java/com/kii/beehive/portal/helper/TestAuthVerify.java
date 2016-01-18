package com.kii.beehive.portal.helper;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.kii.beehive.portal.store.entity.AuthInfoEntry;

public class TestAuthVerify {


	private AuthInfoEntry entry=null;

	@Before
	public void init(){

		String[] permissArray={
			"GET /things/*","GET /things/types","POST /things/*/tags/*","DELETE /things/*/tags/*"
		};

		entry=new AuthInfoEntry("foo", "some_token", new HashSet<>(Arrays.asList(permissArray)));

	}

	@Test
	public void test(){

		assertTrue(entry.doValid("/things/types","GET"));

		assertFalse(entry.doValid("/things/types","POST"));

		assertTrue(entry.doValid("/things/abc/tags/123","POST"));

		assertTrue(entry.doValid("/things/abc","GET"));

//		assertFalse(entry.doValid("http://localhost:7070/mock/api/things/abc/tags/123","POST"));


	}
}
