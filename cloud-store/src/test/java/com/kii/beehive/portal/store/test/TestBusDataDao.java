package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.ruleengine.service.BusinessObjDao;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;

public class TestBusDataDao extends  TestTemplate{
	
	@Autowired
	private BusinessObjDao dao;

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
