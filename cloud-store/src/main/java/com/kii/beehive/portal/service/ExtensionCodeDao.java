package com.kii.beehive.portal.service;

import javax.script.ScriptException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.common.utils.JavaScriptCheck;
import com.kii.beehive.portal.exception.JSFormatErrorException;
import com.kii.beehive.portal.store.entity.ExtensionCodeEntity;
import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.serviceextension.HookGeneral;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;
import com.kii.extension.sdk.service.ServiceExtensionService;

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


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceExtensionService  service;

	private List<ExtensionCodeEntity> getAllEntityByAppID(String appID){

		QueryParam query= ConditionBuilder.orCondition().equal("appID",appID).equal("appID","*").getFinalQueryParam();

		return super.fullQuery(query);

	}



	public void deployScriptToApp(@AppBindParam String appID){


		ScriptCombine combine=getScriptContextByAppID(appID);


		if(StringUtils.isEmpty(combine.getScript())){
			return;
		}

		if(StringUtils.isEmpty(combine.hookConfig)) {
			service.deployServiceExtension(combine.getScript());
		}else{
			service.deployServiceExtension(combine.getScript(),combine.getHookConfig());
		}

	}

	private ScriptCombine getScriptContextByAppID(String appID){

		List<ExtensionCodeEntity> list=getAllEntityByAppID(appID);

		StringBuilder sb=new StringBuilder();
		HookGeneral  hookGeneral=HookGeneral.getInstance();

		list.forEach(entity->{

			sb.append("\n").append(entity.getJsBody()).append("\n");

			if(entity.getEventTrigger()!=null) {
				hookGeneral.addEventTriggerConfigs(entity.getEventTrigger());
			}
			if(entity.getScheduleTrigger()!=null){
				hookGeneral.addScheduleTriggerConfigs(entity.getScheduleTrigger());
			}
		});

		ScriptCombine script=new ScriptCombine();
		script.script=sb.toString();
		script.hookConfig=hookGeneral.generJson(mapper);


		return script;

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

	public ScriptCombine getCurrentServiceCodeByVersion(){

		ScriptCombine  combine=new ScriptCombine();

		String version=service.getCurrentVersion();
		combine.script=service.getServiceExtension(version);

		combine.hookConfig=service.getHookConfig(version);

		return combine;
	}

	public static class ScriptCombine{

		private String script;

		private String hookConfig;

		public String getScript() {
			return script;
		}


		public String getHookConfig() {
			return hookConfig;
		}

	}
}
