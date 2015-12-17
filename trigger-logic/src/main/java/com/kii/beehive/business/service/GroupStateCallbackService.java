package com.kii.beehive.business.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerStatusDao;
import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.exception.StaleVersionedObjectException;
import com.kii.extension.sdk.service.ThingIFService;

@Component
public class GroupStateCallbackService {

	@Autowired
	private TriggerRecordDao  triggerDao;

	@Autowired
	private GlobalThingDao thingDao;

	@Autowired
	private TriggerStatusDao statusDao;


	public void onPositiveArrive(String thingID,String triggerID){

		doSaveOperate(triggerID,thingID,true);
	}


	public void onNegitiveArrive(String thingID,String triggerID){

		doSaveOperate(triggerID,thingID,false);

	}


	private boolean doSaveOperate(String triggerID,String thingID,boolean sign){

		int count=5;

		while(count>0){

			try{
				return checkAndSaveStatus(triggerID,thingID,sign);
			}catch(StaleVersionedObjectException e){
				count--;
			}
		}
		return false;
	}

	private boolean checkAndSaveStatus(String triggerID,String thingID,boolean sign){


		TriggerRuntimeState  state=statusDao.getObjectByID(triggerID);
		boolean oldState=state.isCurrentStatus();

		state.setMemberStatus(thingID,sign);

		boolean result=state.checkPolicy();

		Map<String,Object> updateMap=new HashMap<>();
		updateMap.put("currentStatus",result);
		updateMap.put(thingID,sign);

		statusDao.updateEntityWithVersion(updateMap,triggerID,state.getVersion());

		return state.getWhenType().checkStatus(oldState,result);
	}

}
