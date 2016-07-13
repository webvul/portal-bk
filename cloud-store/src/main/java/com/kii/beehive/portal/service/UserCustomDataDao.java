package com.kii.beehive.portal.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.store.entity.UserCustomData;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class UserCustomDataDao extends AbstractDataAccess<UserCustomData> {

	@Override
	protected Class getTypeCls() {
		return UserCustomData.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("userCustomData");
	}


	public void setUserData(CustomData data, String type,String userID){

		if(super.checkExist(userID)){

			Map<String,Object> map=new HashMap<>();
			map.put(type,data);

			super.updateEntity(map,userID);

		}else{

			UserCustomData  userData=new UserCustomData();

			userData.addData(type,data);

			super.addEntity(userData,userID);
		}
	}

	public CustomData getUserData( String type,String userID){


		UserCustomData  userData=super.getObjectByID(userID);


		return userData.getDataMap().get(type);
	}


}
