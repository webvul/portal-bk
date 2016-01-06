package com.kii.beehive.business.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.TargetAction;
import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.TriggerTarget;
import com.kii.extension.sdk.exception.StaleVersionedObjectException;

@Component
public class TriggerFireCallbackService {

	public static final int MAX_RETRY = 5;
	@Autowired
	private TriggerRecordDao  triggerDao;


	@Autowired
	private TriggerRuntimeStatusDao statusDao;

	@Autowired
	private ThingTagService  thingService;


	@Autowired
	private KiiCommandService commandService;


	private void doCommand(String triggerID){

		List<TriggerTarget>  targets=triggerDao.getObjectByID(triggerID).getTargets();

		targets.forEach(target->{

			TargetAction action=target.getCommand();


			List<GlobalThingInfo>  thingList=thingService.getThingInfos(target.getSelector());

			thingList.forEach(thing->{

					commandService.sendCmdToThing(thing,action,triggerID);
			});

		});

	}

	private boolean verify(String thingID,String triggerID){


		TriggerRuntimeState state=statusDao.getObjectByID(triggerID);

		return state.getThingIDSet().contains(thingID);

	}

	public void onSimpleArrive(String thingID,String triggerID){
		if(!verify(thingID,triggerID)) {
			return;
		}

		doCommand(triggerID);

	}

	public void onSummaryTriggerArrive(String triggerID){

		doCommand(triggerID);

	}

	public void onPositiveArrive(String thingID,String triggerID){

		if(!verify(thingID,triggerID)) {
			return;
		}
		boolean sign=doSaveOperate(triggerID,thingID,true);

		if(sign){
			doCommand(triggerID);
		}
	}


	public void onNegitiveArrive(String thingID,String triggerID){

		if(!verify(thingID,triggerID)) {
			return;
		}

		boolean sign=doSaveOperate(triggerID,thingID,false);


		if(sign){
			doCommand(triggerID);
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
