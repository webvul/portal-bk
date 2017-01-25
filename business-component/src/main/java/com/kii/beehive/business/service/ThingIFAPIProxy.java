package com.kii.beehive.business.service;


import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.helper.FederatedAuthTokenBindTool;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindTool;

@Component
public class ThingIFAPIProxy {
	
	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private AppInfoDao appInfoDao;
	
	public <T>  T doExecWithRealThingID(@AppBindParam(tokenBind = TokenBindTool.BindType.Custom,customBindName= FederatedAuthTokenBindTool.FEDERATED)  String kiiAppID, String thingID, Function<String,T> function){
		
		T result=function.apply(thingID);
		
		return result;
	}
	
	public <T>  T doExec(@AppBindParam(tokenBind = TokenBindTool.BindType.Custom,customBindName= FederatedAuthTokenBindTool.FEDERATED)  String kiiAppID, Supplier<T> function){
		
		T result=function.get();
		
		return result;
	}
}
