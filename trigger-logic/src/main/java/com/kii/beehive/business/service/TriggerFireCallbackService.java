package com.kii.beehive.business.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.extension.sdk.exception.StaleVersionedObjectException;

@Component
public class TriggerFireCallbackService {

	public static final int MAX_RETRY = 5;

	@Autowired
	private TriggerRuntimeStatusDao statusDao;

	@Autowired
	private CommandExecuteService commandService;

	@Autowired
	private BusinessEventListenerService listenerService;



	private boolean verify(String thingID,String triggerID){


		TriggerRuntimeState state=statusDao.getObjByID(triggerID);

		if(state==null){
			return false;
		}

		return state.getThingIDSet().contains(thingID);

	}

	public void onSimpleArrive(String thingID,String triggerID){
		if(!verify(thingID,triggerID)) {
			return;
		}

		commandService.doCommand(triggerID);

	}

	public void onSummaryTriggerArrive(String triggerID){

		commandService.doCommand(triggerID);

	}

	public void onPositiveArrive(String thingID,String triggerID){

		if(!verify(thingID,triggerID)) {
			return;
		}
		boolean sign=doSaveOperate(triggerID,thingID,true);

		if(sign){
			commandService.doCommand(triggerID);
		}
	}


	public void onNegativeArrive(String thingID,String triggerID){

		if(!verify(thingID,triggerID)) {
			return;
		}

		boolean sign=doSaveOperate(triggerID,thingID,false);


		if(sign){
			commandService.doCommand(triggerID);
		}

	}


	private boolean doSaveOperate(String triggerID,String thingID,boolean sign){

		int count= MAX_RETRY;

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


		GroupTriggerRuntimeState state=statusDao.getGroupRuntimeState(triggerID);
		if(state==null){
			listenerService.disableTriggerByTargetID(triggerID);
			return false;
		}
		boolean oldState=state.isCurrentStatus();

		state.getMemberState().setMemberStatus(thingID,sign);

		boolean result=state.checkPolicy();

		Map<String,Object> updateMap=new HashMap<>();
		updateMap.put("currentStatus",result);
		updateMap.put(thingID,sign);

		statusDao.updateEntityWithVersion(updateMap,triggerID,state.getVersion());

		return state.getWhenType().checkStatus(oldState,result);
	}

}
