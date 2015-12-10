package com.kii.extension.sdk.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.service.ServiceExtensionService;
import com.kii.extension.sdk.service.UserService;

public class TestServerExten extends TestTemplate{


	@Autowired
	private ServiceExtensionService service;



	@Before
	public void init(){

		resolver.setAppName("test-slave-1");

	}

	@Test
	public void deployServiceExtension(){


	}

	@Test
	public void deployServiceExtensionWithHook(){

	}

}
