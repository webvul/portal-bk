package com.kii.beehive.portal.manager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.business.service.ServiceExtensionDeployService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.InvalidAuthException;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
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

	public void enableTrigger(String triggerID) {

		String userID= AuthInfoStore.getUserID();

		TriggerRecord record=triggerDao.getTriggerRecord(triggerID);

		if(userID.equals(record.getUserID()) && !AuthInfoStore.isAmin()){
			throw new InvalidAuthException(userID,record.getUserID());
		}
		if(record.getRecordStatus()== TriggerRecord.StatusType.disable) {
			triggerDao.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.enable), triggerID);
		}
	}

	public void disableTrigger(String triggerID) {

		String userID= AuthInfoStore.getUserID();

		TriggerRecord record=triggerDao.getTriggerRecord(triggerID);

		if(userID.equals(record.getUserID()) && !AuthInfoStore.isAmin()){
			throw new InvalidAuthException(userID,record.getUserID());
		}

		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {

			triggerDao.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.disable), triggerID);
		}

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

		list.add(stateChange());

		list.add( getStateCreated());

		list.add(thingCreated());

		list.add(thingRemoved());

		return list;
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
