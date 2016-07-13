package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.UserCustomDataDao;
import com.kii.beehive.portal.store.entity.CustomData;

public class TestUserCustomDao extends  TestTemplate{


	@Autowired
	private UserCustomDataDao  dao;

	@Test
	public void  testCustom(){

		CustomData data=new CustomData();

		data.addData("foo","bar");
		data.addData("abc",123);

		dao.setUserData(data,"test","abc");


		CustomData  newData=dao.getUserData("test","abc");

		assertEquals(newData.getData().get("foo"),"bar");

	}

}
