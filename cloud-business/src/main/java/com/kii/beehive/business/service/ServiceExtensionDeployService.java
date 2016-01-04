package com.kii.beehive.business.service;

import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.ExtensionCodeDao;
import com.kii.beehive.portal.store.entity.ExtensionCodeEntity;
import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.entity.serviceextension.HookGeneral;
import com.kii.extension.sdk.service.ServiceExtensionService;

@Component
public class ServiceExtensionDeployService {



	@Autowired
	private ExtensionCodeDao extensionCodeDao;

	@Autowired
	private ServiceExtensionService service;

	@Autowired
	private ObjectMapper mapper;



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

		List<ExtensionCodeEntity> list=extensionCodeDao.getAllEntityByAppID(appID);

		StringBuilder sb=new StringBuilder();
		HookGeneral hookGeneral=HookGeneral.getInstance();

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


	public ScriptCombine getCurrentServiceCodeByVersion(@AppBindParam String appID){

		ScriptCombine  combine=new ScriptCombine();

		String version=service.getCurrentVersion();
		combine.script=service.getServiceExtension(version);

		combine.hookConfig=service.getHookConfig(version);

		return combine;
	}


	public <T> T callServiceExtension(@AppBindParam String appID,String serviceName,Object param,  Class<T> cls){

		return service.callServiceExtension(serviceName,param,cls);
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
