package com.kii.beehive.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.CreateResponse;
import com.kii.extension.sdk.service.DataService;

@Component
public class DemoCrossAppDao {

	@Autowired
	private DataService service;
	

	public String addData(@AppBindParam String appName,FooEntity foo){

		BucketInfo bucket=new BucketInfo("demo");
		CreateResponse resp=service.createObject(foo, bucket);

		return resp.getObjectID();
	}

	public FooEntity getData(@AppBindParam String appName,String id){

		return service.getObjectByID(id,new BucketInfo("demo"),FooEntity.class);
	}

	public static class FooEntity{

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
