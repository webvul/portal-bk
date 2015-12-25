package com.kii.beehive.business.manager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.business.service.KiiTriggerRegistService;
import com.kii.beehive.business.service.ThingGroupStateService;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.ExtensionCodeDao;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.ExtensionCodeEntity;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.extension.sdk.entity.serviceextension.BucketWhenType;
import com.kii.extension.sdk.entity.serviceextension.EventTriggerConfig;
import com.kii.extension.sdk.entity.serviceextension.TriggerFactory;
import com.kii.extension.sdk.entity.serviceextension.TriggerScopeType;

@Component
@Transactional
public class BeehiveTriggerManager {


	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private KiiTriggerRegistService  registService;

	@Autowired
	private ThingGroupStateService groupService;

	@Autowired
	private GlobalThingDao thingDao;

	@Autowired
	private ExtensionCodeDao  extensionDao;

	@Autowired
	private ResourceLoader loader;


	public String  createTrigger(TriggerRecord record){


		String triggerID=triggerDao.addEntity(record).getObjectID();


		TriggerSource source=record.getSource();

		if(source.getThingList().size()==1) {
			GlobalThingInfo thing=thingDao.getThingByVendorThingID(source.getThingList().iterator().next());
			registService.registSingleTrigger(thing.getKiiThingID(),record.getPerdicate(),triggerID);

		}else {
			List<GlobalThingInfo> things = thingDao.getThingsByVendorIDArray(source.getThingList());

			things.forEach(thing->{
				registService.registDoubleTrigger(thing.getKiiThingID(),record.getPerdicate().getCondition(),triggerID);
			});

			groupService.createThingGroup(things,record.getPerdicate().getTriggersWhen(),triggerID,source);
		}

		return triggerID;
	}



	public void initAppForTrigger(){


		try {
			initCommon();

			initOnTriggerFire();

			initStateUpload();



		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}

	public void deployTriggerToAll(){

		appInfoDao.getSalveAppList().forEach(appInfo->{

			extensionDao.deployScriptToApp(appInfo.getAppID());

		});
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
		trigger1.setEndpoint("global_onThingStateChange");
		list.add(trigger1);

		EventTriggerConfig trigger2= TriggerFactory.getBucketInstance("_states", BucketWhenType.DATA_OBJECT_CREATED,TriggerScopeType.App);
		trigger2.setEndpoint("global_onThingStateChange");
		list.add(trigger2);
		return list;
	}

	private void initCommon() throws IOException {
		String jsStateUpload= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/trigger/script/common.js").getInputStream(), Charsets.UTF_8);
		ExtensionCodeEntity uploadEntity=new ExtensionCodeEntity();
		uploadEntity.setFunctionName("common_utils");

		uploadEntity.setJsBody(jsStateUpload);

		extensionDao.addGlobalExtensionCode(uploadEntity);
	}

}
