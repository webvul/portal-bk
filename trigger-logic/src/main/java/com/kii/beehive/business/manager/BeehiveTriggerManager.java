package com.kii.beehive.business.manager;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.business.service.GroupStateCallbackService;
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
import com.kii.extension.sdk.entity.serviceextension.ThingWhenType;
import com.kii.extension.sdk.entity.serviceextension.TriggerFactory;
import com.kii.extension.sdk.entity.serviceextension.TriggerScopeType;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;

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
			List<GlobalThingInfo> things = thingDao.getThingsByIDArray(source.getThingList());

			things.forEach(thing->{
				registService.registDoubleTrigger(thing.getKiiThingID(),record.getPerdicate().getCondition(),triggerID);
			});

			groupService.createThingGroup(things,record.getPerdicate().getTriggersWhen(),triggerID,source);
		}

		return triggerID;
	}

	public void initAppForTrigger(){


		try {
			initOnStateChange();

			initStateUpload();

			appInfoDao.getSalveAppList().forEach(appInfo->{

				extensionDao.deployScriptToApp(appInfo.getAppID());

			});

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}

	private void initOnStateChange() throws IOException {
		String jsStatusChange= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/trigger/script/onThingStatusChange.js").getInputStream(), Charsets.UTF_8);

		ExtensionCodeEntity entity=new ExtensionCodeEntity();
		entity.setFunctionName("thing_status_change");

		EventTriggerConfig<BucketWhenType> trigger= TriggerFactory.getBucketInstance("_state", TriggerScopeType.App);
		trigger.setWhen(BucketWhenType.DATA_OBJECT_UPDATED);
		entity.setEventTrigger(trigger);

		entity.setJsBody(jsStatusChange);

		extensionDao.addGlobalExtensionCode(entity);
	}

	private void initStateUpload() throws IOException {
		String jsStateUpload= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/trigger/script/stateUpload.js").getInputStream(), Charsets.UTF_8);
		ExtensionCodeEntity uploadEntity=new ExtensionCodeEntity();
		uploadEntity.setFunctionName("state_upload_for_group");

		uploadEntity.setJsBody(jsStateUpload);

		extensionDao.addGlobalExtensionCode(uploadEntity);
	}

}
