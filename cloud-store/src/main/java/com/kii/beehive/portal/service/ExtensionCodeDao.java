package com.kii.beehive.portal.service;

import javax.script.ScriptException;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.common.utils.JavaScriptCheck;
import com.kii.beehive.portal.exception.JSFormatErrorException;
import com.kii.beehive.portal.store.entity.ExtensionCodeEntity;
import com.kii.extension.ruleengine.sdk.annotation.BindAppByName;
import com.kii.extension.ruleengine.sdk.entity.BucketInfo;
import com.kii.extension.ruleengine.sdk.query.ConditionBuilder;
import com.kii.extension.ruleengine.sdk.query.QueryParam;
import com.kii.extension.ruleengine.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class ExtensionCodeDao extends AbstractDataAccess<ExtensionCodeEntity> {
	@Override
	protected Class<ExtensionCodeEntity> getTypeCls() {
		return ExtensionCodeEntity.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("extensionCode");
	}





	public List<ExtensionCodeEntity> getAllEntityByAppID(String appID){

		QueryParam query= ConditionBuilder.orCondition().equal("appID",appID).equal("appID","*").getFinalQueryParam();

		return super.fullQuery(query);

	}




	public boolean checkFunctionExisted(String functionName,String appID){

		if(StringUtils.isEmpty(appID)){

			appID="global";
		}

		String id=appID+"_"+functionName;


		return super.checkExist(id);

	}

	public void addGlobalExtensionCode(ExtensionCodeEntity entity){
		entity.setAppID("*");


		try {
			 JavaScriptCheck.checkJSFormat(entity.getJsBody());

		}catch(ScriptException ex){
			throw new JSFormatErrorException(ex);
		}


		super.addEntity(entity,"global_"+entity.getFunctionName());
	}

	public void addEtensionCodeToApp(ExtensionCodeEntity entity,String appID){

		entity.setAppID(appID);

		try {
			JavaScriptCheck.checkJSFormat(entity.getJsBody());

		}catch(ScriptException ex){
			throw new JSFormatErrorException(ex);
		}

		String id=appID+"_"+entity.getFunctionName();

		super.addEntity(entity,id);
	}




}
