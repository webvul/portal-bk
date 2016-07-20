package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.fail;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import com.kii.extension.sdk.service.DataService;

public class TestCheckExist extends  TestTemplate {

	@Autowired
	private DataService service;

	@Autowired
	private AppBindToolResolver  resolver;


	@Test
	public void testCheckExists(){

		resolver.pushAppName("portal");

		try {
			 service.checkObjectExist("abc", new BucketInfo("foo"));
			 fail();
		}catch(ObjectNotFoundException e){
			return;
		}
		fail();

	}

	@Test
	public void testAddDataToNull(){
		resolver.pushAppName("portal");


		Object obj=service.getObjectByID("xyz",new BucketInfo("foo"),Map.class);

	}

}
