package com.kii.extension.sdk.test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.service.AppMasterSalveService;

public class TestPortal extends TestTemplate{

	@Autowired
	private AppMasterSalveService appTool;


	private String master="master-test";
	private String[] salves={"test-slave-1","test-slave-2"};


	@Test
	public void checkMaster(){

//		assertFalse(appTool.isMaster(salves[0]));
//
//		String value=appTool.checkMaster(salves[0]);

//		System.out.println(value);
	}

	@Test
	public void testSetMaster(){

//		appTool.setMaster(master);
//
//		assertTrue(appTool.isMaster(master));


//		AppMasterSalveService.ClientInfo info=appTool.addSalveApp(master,salves[0]);
//
////		assertNotNull(info.getClientID());
//
//		appTool.registInSalve(info,master,salves[0]);

	}


}
