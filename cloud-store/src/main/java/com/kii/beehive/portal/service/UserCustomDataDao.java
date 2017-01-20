package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.store.entity.UserCustomData;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind= TokenBindTool.BindType.Custom,customBindName = PortalTokenBindTool.PORTAL_OPER )
@Component
public class UserCustomDataDao extends BaseKiicloudDao<UserCustomData> {

	@Override
	protected Class getTypeCls() {
		return UserCustomData.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("userCustomData");
	}


	public void setUserData(CustomData data, String type, String userID) {

		if (super.checkExist(userID)) {


			super.updateEntity(Collections.singletonMap(type,""),userID);

			super.updateEntity(Collections.singletonMap(type,data),userID);

		} else {

			UserCustomData userData = new UserCustomData();

			userData.addData(type, data);

			super.addEntity(userData, userID);
		}
	}

	public CustomData getUserData(String type, String userID) {


		try {

			UserCustomData userData = super.getObjectByID(userID);
			Map<String, CustomData> a = userData.getDataMap();
			CustomData c = a.get(type);
			return c;

		} catch (ObjectNotFoundException e) {
			return new CustomData();
		}
	}


}
