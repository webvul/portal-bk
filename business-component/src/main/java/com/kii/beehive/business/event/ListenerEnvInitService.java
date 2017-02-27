package com.kii.beehive.business.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.business.service.ServiceExtensionDeployService;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveParameterDao;
import com.kii.beehive.portal.service.ExtensionCodeDao;
import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.beehive.portal.store.entity.ExtensionCodeEntity;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.serviceextension.BucketWhenType;
import com.kii.extension.sdk.entity.serviceextension.EventTriggerConfig;
import com.kii.extension.sdk.entity.serviceextension.ThingWhenType;
import com.kii.extension.sdk.entity.serviceextension.TriggerFactory;
import com.kii.extension.sdk.entity.serviceextension.TriggerScopeType;

@Component
public class ListenerEnvInitService {

	private Logger log= LoggerFactory.getLogger(ListenerEnvInitService.class);

	@Autowired
	private ExtensionCodeDao extensionDao;

	@Autowired
	private AppBindToolResolver appBindToolResolver;

	@Autowired
	private TokenBindToolResolver tokenBindToolResolver;

	@Autowired
	private BeehiveParameterDao parameterDao;

	@Autowired
	private AppInfoDao appInfoDao;


	@Autowired
	private ResourceLoader loader;

	@Autowired
	private ServiceExtensionDeployService extensionService;

	public void initExtensionCodeScript(){


		try {
			initCommon();


			initStateUpload();

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}





	private void initStateUpload() throws IOException {

		String jsStateUpload= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/serviceExtension/stateUpload.js").getInputStream(), Charsets.UTF_8);
		ExtensionCodeEntity uploadEntity=new ExtensionCodeEntity();
		uploadEntity.setFunctionName("state_upload_for_group");

		List<EventTriggerConfig> list = getTriggerEndpoint();

		uploadEntity.setEventTrigger(list);

		uploadEntity.setJsBody(jsStateUpload);

		extensionDao.addGlobalExtensionCode(uploadEntity);

	}


	private void initCommon() throws IOException {
		String jsStateUpload= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/serviceExtension/common.js").getInputStream(), Charsets.UTF_8);
		ExtensionCodeEntity uploadEntity=new ExtensionCodeEntity();
		uploadEntity.setFunctionName("common_utils");

		uploadEntity.setJsBody(jsStateUpload);

		extensionDao.addGlobalExtensionCode(uploadEntity);
	}

	private List<EventTriggerConfig> getTriggerEndpoint() {
		List<EventTriggerConfig> list=new ArrayList<>();

		list.add(stateChange());

		list.add(getStateCreated());

		list.add(thingCreated());

		list.add(thingRemoved());

		list.add(thingCmdResponse());

		return list;
	}

	private EventTriggerConfig thingCmdResponse() {
		EventTriggerConfig trigger3= TriggerFactory.getBucketInstance("_commands",BucketWhenType.DATA_OBJECT_UPDATED,TriggerScopeType.Things);
		trigger3.setEndpoint(EndPointNameConstant.OnThingCmdResponse);
		return trigger3;
	}

	private EventTriggerConfig thingRemoved() {
		EventTriggerConfig trigger3= TriggerFactory.getThingInstance(ThingWhenType.THING_DELETED);
		trigger3.setEndpoint(EndPointNameConstant.OnThingRemoved);
		return trigger3;
	}

	private EventTriggerConfig thingCreated() {
		EventTriggerConfig trigger3= TriggerFactory.getThingInstance(ThingWhenType.THING_CREATED);
		trigger3.setEndpoint(EndPointNameConstant.OnThingCreated);
		return trigger3;
	}

	private EventTriggerConfig getStateCreated() {
		EventTriggerConfig trigger2= TriggerFactory.getBucketInstance("_states", BucketWhenType.DATA_OBJECT_CREATED, TriggerScopeType.App);
		trigger2.setEndpoint(EndPointNameConstant.OnThingStateChange);
		return trigger2;
	}

	private EventTriggerConfig stateChange() {
		EventTriggerConfig trigger1 = TriggerFactory.getBucketInstance("_states", BucketWhenType.DATA_OBJECT_UPDATED, TriggerScopeType.App);
		trigger1.setEndpoint(EndPointNameConstant.OnThingStateChange);
		return trigger1;
	}

	/**
	 * deploy server code to each slave apps,
	 * and set trigger call back param(bucket)
	 *
	 * @param param
     */
	public void deployTriggerToAll(CallbackUrlParameter param){

		log.info("deployTriggerToAll start");

		// upload server code to bucket "extensionCode" of portal app
		this.initExtensionCodeScript();

		// for each slave app, deploy server code to it, and set trigger call back param(bucket)
		appInfoDao.getSlaveAppList().forEach(appInfo->{

			// use admin token inside the loop
			// otherwise, the operation in each slave app may be impacted by the user token set in above this.initExtensionCodeScript();
			appBindToolResolver.clearAll();
			tokenBindToolResolver.bindAdmin();

			extensionService.deployScriptToApp(appInfo.getAppID());

			parameterDao.saveTriggerCallbackParam(appInfo.getAppID(),param);

		});

		log.info("deployTriggerToAll end");

	}

	public void deployTrigger(AppInfo appInfo,CallbackUrlParameter param){


		extensionService.deployScriptToApp(appInfo.getAppID());

		parameterDao.saveTriggerCallbackParam(appInfo.getAppID(),param);
	}


}
