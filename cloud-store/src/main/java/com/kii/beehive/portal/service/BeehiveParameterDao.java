package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

//@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class BeehiveParameterDao extends AbstractDataAccess<CallbackUrlParameter>{

	public void saveTriggerCallbackParam(@AppBindParam  String appName, CallbackUrlParameter param){

		super.addEntity(param,"beehive_callback_url");
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
