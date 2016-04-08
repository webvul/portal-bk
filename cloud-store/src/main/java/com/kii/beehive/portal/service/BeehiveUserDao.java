package com.kii.beehive.portal.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class BeehiveUserDao extends AbstractDataAccess<BeehiveUser> {

	public List<BeehiveUser> getUserByIDs(String[] userIDList) {

		return super.getEntitys(userIDList);
	}
	
	public BeehiveUser getUserByID(String userID) {

		return super.getObjectByID(userID);

	}
	
	public void deleteUser(String userID) {

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
