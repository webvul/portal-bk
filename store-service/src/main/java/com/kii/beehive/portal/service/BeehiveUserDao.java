package com.kii.beehive.portal.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal")
@Component
public class BeehiveUserDao extends AbstractDataAccess<BeehiveUser> {


	public void createUser(BeehiveUser user){

		super.addEntity(user, user.getKiiUserID());


	}

	public Map<String,Object> getUserCustomInfoByID(String userID,Set<String> field){

		BeehiveUser user=super.getObjectByID(userID);


		return user.getCustomFields().filter(field);

	}

	public void updateUserCustomFields(String userID,Map<String,Object> fieldMap){

		Map<String,Object> paramMap=new HashMap<>();

		paramMap.putAll(fieldMap);

		super.updateEntity(paramMap, userID);

	}

	public void deleteUser(String userID){
		super.removeEntity(userID);
	}

	@Override
	protected Class<BeehiveUser> getTypeCls() {
		return BeehiveUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehiveUser");
	}
}
