package com.kii.beehive.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

//@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class BeehiveParameterDao extends AbstractDataAccess<CallbackUrlParameter>{

	@Autowired
	private AppBindToolResolver resolver;

	private static final String CALLBACK_URL = "beehive_callback_url";

	public void saveTriggerCallbackParam(@AppBindParam  String appID, CallbackUrlParameter param){

		super.addEntity(param, CALLBACK_URL);
	}

	public boolean verify(@AppBindParam AppInfo appInfo, String token){

		resolver.setToken(token);

		return super.checkExist(CALLBACK_URL);

	}

	@Override
	protected Class<CallbackUrlParameter> getTypeCls() {
		return CallbackUrlParameter.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehive_parameters");
	}
}
