package com.kii.beehive.business.manager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.business.service.ServiceExtensionDeployService;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveParameterDao;
import com.kii.beehive.portal.service.ExtensionCodeDao;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.beehive.portal.store.entity.ExtensionCodeEntity;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.serviceextension.BucketWhenType;
import com.kii.extension.sdk.entity.serviceextension.EventTriggerConfig;
import com.kii.extension.sdk.entity.serviceextension.ThingWhenType;
import com.kii.extension.sdk.entity.serviceextension.TriggerFactory;
import com.kii.extension.sdk.entity.serviceextension.TriggerScopeType;
import com.kii.extension.sdk.exception.ObjectNotFoundException;

@Component
public class TriggerMaintainManager {



	@Autowired
	private AppInfoDao appInfoDao;


	@Autowired
	private ServiceExtensionDeployService extensionService;

	@Autowired
	private ExtensionCodeDao  extensionDao;


	@Autowired
	private TriggerRuntimeStatusDao  statusDao;

	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private ResourceLoader loader;


	@Autowired
	private BeehiveParameterDao parameterDao;

	public void initAppForTrigger(){


		try {
			initCommon();

			initOnTriggerFire();

			initStateUpload();



		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}

	public void deployTriggerToAll(CallbackUrlParameter  param){

		appInfoDao.getSalveAppList().forEach(appInfo->{

			extensionService.deployScriptToApp(appInfo.getAppID());

			parameterDao.saveTriggerCallbackParam(appInfo.getAppID(),param);

		});
	}

	public void disableTrigger(String triggerID){

		triggerDao.enableTrigger(triggerID);
	}

	public void enableTrigger(String triggerID){

		triggerDao.disableTrigger(triggerID);

	}



	private void initOnTriggerFire() throws IOException {
		String jsStatusChange= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/trigger/script/onTriggerBeenFired.js").getInputStream(), Charsets.UTF_8);

		ExtensionCodeEntity entity=new ExtensionCodeEntity();
		entity.setFunctionName("trigger_been_fire");


		entity.setJsBody(jsStatusChange);

		extensionDao.addGlobalExtensionCode(entity);
	}

	private void initStateUpload() throws IOException {

		String jsStateUpload= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/trigger/script/stateUpload.js").getInputStream(), Charsets.UTF_8);
		ExtensionCodeEntity uploadEntity=new ExtensionCodeEntity();
		uploadEntity.setFunctionName("state_upload_for_group");

		List<EventTriggerConfig> list = getTriggerEndpoint();

		uploadEntity.setEventTrigger(list);

		uploadEntity.setJsBody(jsStateUpload);

		extensionDao.addGlobalExtensionCode(uploadEntity);
	}

	private List<EventTriggerConfig> getTriggerEndpoint() {
		List<EventTriggerConfig> list=new ArrayList<>();

		EventTriggerConfig trigger1= TriggerFactory.getBucketInstance("_states", BucketWhenType.DATA_OBJECT_UPDATED, TriggerScopeType.App);
		trigger1.setEndpoint(EndPointNameConstant.OnThingStateChange);
		list.add(trigger1);

		EventTriggerConfig trigger2= TriggerFactory.getBucketInstance("_states", BucketWhenType.DATA_OBJECT_CREATED,TriggerScopeType.App);
		trigger2.setEndpoint(EndPointNameConstant.OnThingStateChange);
		list.add(trigger2);


		EventTriggerConfig trigger3= TriggerFactory.getThingInstance(ThingWhenType.THING_CREATED);
		trigger3.setEndpoint(EndPointNameConstant.OnThingCreated);
		list.add(trigger3);
		return list;
	}

	private void initCommon() throws IOException {
		String jsStateUpload= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/trigger/script/common.js").getInputStream(), Charsets.UTF_8);
		ExtensionCodeEntity uploadEntity=new ExtensionCodeEntity();
		uploadEntity.setFunctionName("common_utils");

		uploadEntity.setJsBody(jsStateUpload);

		extensionDao.addGlobalExtensionCode(uploadEntity);
	}
	
	public TriggerRecord getTriggerRecord(String triggerID) {
		return  triggerDao.getObjectByID(triggerID);
	}
}
