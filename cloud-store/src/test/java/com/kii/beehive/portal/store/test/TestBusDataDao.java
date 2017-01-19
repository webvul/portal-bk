package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.BeehiveConfigDao;
import com.kii.beehive.portal.service.PortalOperateUserService;
import com.kii.extension.ruleengine.service.BusinessObjDao;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;

public class TestBusDataDao extends  TestTemplate{
	
	@Autowired
	private BusinessObjDao dao;
	
	@Autowired
	private BeehiveConfigDao configDao;

	@Autowired
	private PortalOperateUserService service;
	
	@Test
	public void testToken(){
		
		service.init();
		String token=service.getToken();
		
		service.init();
		
		String token2=service.getToken();
		
		assertEquals(token,token2);
	}
	
	@Test
	public void testConfig(){
		
		configDao.getAll();
	}
	
	@Test
	public void testBusinessObj(){
		
		BusinessDataObject obj=new BusinessDataObject("abc",null, BusinessObjType.Business);
		
		dao.addBusinessObj(obj);
		
		obj.getData().put("foo","bar");
		
		dao.addBusinessObj(obj);
		
		BusinessDataObject newObj=dao.getObjectByID(obj.getFullID());
		
		assertEquals("bar",newObj.getData().get("foo"));
		
	}
	
}
