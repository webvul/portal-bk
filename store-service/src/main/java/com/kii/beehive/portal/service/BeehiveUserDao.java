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

		super.addEntity(user, user.getBeehiveUserID());

	}

	public BeehiveUser getUserCustomInfoByID(String userID,Set<String> field){

		BeehiveUser user=super.getObjectByID(userID);

		user.getCustomFields().keySet().removeIf((k) -> {
			String key = k.substring(BeehiveUser.PREFIX.length() + 1);
			return !field.contains(key);
		});

		return user;

	}

	public void updateUserCustomFields(String userID,Map<String,Object> fieldMap){

		Map<String,Object> paramMap=new HashMap<>();

		fieldMap.entrySet().stream().forEach(e->paramMap.put(BeehiveUser.PREFIX+e.getKey(),e.getValue()));

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
