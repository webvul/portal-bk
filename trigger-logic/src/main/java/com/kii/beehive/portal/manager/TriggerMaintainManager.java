package com.kii.beehive.business.manager;


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
	private ServiceExtensionDeployService extensionService;



	@Autowired
	private TriggerRuntimeStatusDao  statusDao;

	@Autowired
	private TriggerRecordDao triggerDao;





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




	
	public TriggerRecord getTriggerRecord(String triggerID) {
		return  triggerDao.getObjectByID(triggerID);
	}
}
