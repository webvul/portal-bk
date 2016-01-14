package com.kii.beehive.portal.manager;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.business.ruleengine.ExpressCompute;
import com.kii.beehive.business.service.KiiCommandService;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.trigger.SummaryExpress;
import com.kii.beehive.portal.store.entity.trigger.SummaryFunctionType;
import com.kii.beehive.portal.store.entity.trigger.SummarySource;
import com.kii.beehive.portal.store.entity.trigger.SummaryStateEntry;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.Predicate;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.entity.thingif.TriggerTarget;
import com.kii.extension.sdk.exception.StaleVersionedObjectException;

@Component
public class ThingStateSummaryManager {


	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private ThingTagManager  thingTagService;

	@Autowired
	private TriggerRecordDao  triggerDao;

	@Autowired
	private KiicloudEventListenerService listenerService;

	@Autowired
	private AppInfoManager appInfoManager;

	@Autowired
	private TriggerRuntimeStatusDao statusDao;

	@Autowired
	private ExpressCompute computer;

	@Autowired
	private KiiCommandService commandService;


	private void fillForAverageCompute(SummaryTriggerRecord record){


		for(SummarySource source:record.getSummarySource().values()){

			  List<SummaryExpress> newList=new ArrayList<>();

			  List<SummaryExpress>  expressList=source.getExpressList();

			  for(SummaryExpress express:expressList){

				  if(express.getFunction()==SummaryFunctionType.Avg){

					  List<SummaryExpress> additionList=express.generAdditionExp();

					  newList.addAll(additionList);
				  }

			  }
			  newList.addAll(expressList);
			  source.setExpressList(newList);
		}


	}

	public void removeTrigger(String triggerID){

		SummaryTriggerRuntimeState state=statusDao.getSummaryRuntimeState(triggerID);

		for(SummaryStateEntry  listener:state.getListeners().values()){

			listenerService.removeListener(listener.getStateListenerID());
			listenerService.removeListener(listener.getTagListenerID());

		}

		triggerDao.deleteTriggerRecord(triggerID);

	}

	public void initStateSummary(SummaryTriggerRecord record){

		fillForAverageCompute(record);

		record.setRecordStatus(TriggerRecord.StatusType.disable);
		String triggerID=triggerDao.addKiiEntity(record);

		SummaryTriggerRuntimeState state=new SummaryTriggerRuntimeState();

		List<String> thingIDList=new ArrayList<>();

		Map<String,Object> globalStateMap=new HashMap<>();

		for(Map.Entry<String,SummarySource> entry:record.getSummarySource().entrySet()) {

			SummarySource summary=entry.getValue();

			String name=entry.getKey();


			TriggerSource source = summary.getSource();

			List<GlobalThingInfo> things = thingTagService.getThingInfos(source.getSelector());

			List<String> thingIDs = things.stream().map(thing -> thing.getFullKiiThingID()).collect(Collectors.toList());

			thingIDList.addAll(thingIDs);

			SummaryStateEntry ids=new SummaryStateEntry();

			ids.setStateListenerID( listenerService.addThingStatusListener(thingIDs, triggerID,name) );

			ids.setThingIDs(thingIDs);

			if (!source.getSelector().getTagList().isEmpty()) {

				String tagListenerID=listenerService.addSummaryChangeListener(source.getSelector().getTagList(), triggerID,name);
				ids.setTagListenerID(tagListenerID);
			}

			ids=refreshThingState(thingIDs,name,summary.getExpressList(),ids);

			globalStateMap.putAll(ids.getSummary());
			state.addListener(ids,name);
		}



		statusDao.saveState(state,triggerID);

		triggerDao.enableTrigger(triggerID);


		if(computer.doExpress(record.getPerdicate().getCondition(),globalStateMap)){

			commandService.doCommand(record);
		}

	}




	private SummaryStateEntry refreshThingState(List<String> thingList,String groupName,List<SummaryExpress> expressList,SummaryStateEntry entry){

		entry.getSummary().clear();
		thingList.forEach(thingID->{


			ThingStatus status=thingIFService.getStatus(thingID);

			updateState(groupName, status, expressList, entry);
		});

		return entry;
	}

	public void onTagChanged(String triggerID,String groupName){


		SummaryTriggerRecord record = (SummaryTriggerRecord) triggerDao.getTriggerRecord(triggerID);

		if(record==null){

			listenerService.disableTriggerByTargetID(triggerID);
			return;
		}

		SummarySource source=record.getSummarySource().get(groupName);

		TagSelector tagSelector=source.getSource().getSelector();
		List<GlobalThingInfo> thingList=thingTagService.getThingInfos(tagSelector);
		List<String> thingIDList=thingList.stream().map(thing->thing.getFullKiiThingID()).collect(Collectors.toList());

		SummaryTriggerRuntimeState state=statusDao.getSummaryRuntimeState(triggerID);
		if(state==null){
			listenerService.disableTriggerByTargetID(triggerID);
			return;
		}

		listenerService.updateThingStatusListener(thingIDList,state.getListeners().get(groupName).getTagListenerID());

		refreshThingState(thingIDList,groupName,record.getSummarySource().get(groupName).getExpressList(),state.getListeners().get(groupName));

	}


	public void computeStateSummary(String  triggerID,String groupName,ThingStatus status){

		SummaryTriggerRecord  trigger= (SummaryTriggerRecord) triggerDao.getTriggerRecord(triggerID);

		if(trigger==null){
			listenerService.disableTriggerByTargetID(triggerID);
			return;
		}

		for(int i=0;i<5;i++) {

			SummaryTriggerRuntimeState state=statusDao.getSummaryRuntimeState(triggerID);
			if(state==null){
				listenerService.disableTriggerByTargetID(triggerID);
				break;
			}
			SummaryStateEntry entry=state.getListeners().get(groupName);

			updateState(groupName, status, trigger.getSummarySource().get(groupName).getExpressList(), entry);

			try {
				statusDao.updateEntityWithVersion(Collections.singletonMap(groupName, entry), triggerID, state.getVersion());

				Map<String,Object> fullState=new HashMap<>();
				state.getListeners().values().forEach(entity->{
					fullState.putAll(entity.getSummary());
				});

				if(computer.doExpress(trigger.getPerdicate().getCondition(),fullState)){

					commandService.doCommand(trigger);
				}

			}catch(StaleVersionedObjectException e){
				continue;
			}
		}



	}

	private void updateState(String groupName, ThingStatus status, List<SummaryExpress>  expressList, SummaryStateEntry entry) {

		Map<String,Object> summary=entry.getSummary();

		for(SummaryExpress express:expressList) {

			if(express.getFunction()==SummaryFunctionType.Avg){
				continue;
			}

			String fieldName = express.getStateName();

			String summaryField = groupName + "." + express.getSummaryAlias();

			Object delta = status.getField(fieldName);
			Object currValue = summary.get(summaryField);

			Object newValue=computeSummary(delta, express.getFunction(), currValue);

			summary.put(summaryField,newValue);
		}


		expressList.stream()
				.filter(expresss-> expresss.getFunction()== SummaryFunctionType.Avg)
				.forEach(express->{

			String sumField=express.getSumField();
			String countField=express.getCountField();

			Number sum= (Number) summary.get(groupName+"."+sumField);

			Number count= (Number) summary.get(groupName+"."+countField);

			String summaryAlias=groupName+"."+express.getSummaryAlias();

			if(count==null){
				summary.put(summaryAlias,0);
			}else {

				double avg = sum.doubleValue() / count.longValue();
				summary.put(summaryAlias,avg);
			}

		});

		entry.setSummary(summary);
		return;
	}

	private Object computeSummary(Object delta, SummaryFunctionType type, Object currValue) {

		if(delta==null){
			currValue=0;
			return currValue;
		}

		try {
			switch (type) {
				case Count:
					if(currValue==null){
						currValue=0;
					}
					return ((Integer) currValue) + 1;
				case Max:
					if(currValue==null){
						currValue=Long.MIN_VALUE;
					}
					if (!isInteger(delta) || !isInteger(currValue)) {
						boolean sign = ((Number) currValue).doubleValue() >= ((Number) delta).doubleValue();
						if (!sign) {
							currValue = delta;
						}
					} else {
						currValue = Math.max(((Number) currValue).longValue(), ((Number) delta).longValue());
					}
					return currValue;
				case Min:
					if(currValue==null){
						currValue=Long.MAX_VALUE;
					}
					if (!isInteger(delta) || !isInteger(currValue)) {
						boolean sign = ((Number) currValue).doubleValue() <= ((Number) delta).doubleValue();
						if (!sign) {
							currValue = delta;
						}
					} else {
						currValue = Math.min(((Number) currValue).longValue(), ((Number) delta).longValue());
					}
					return currValue;
				case Sum:
					if(currValue==null){
						currValue=0l;
					}
					if (!isInteger(delta) || !isInteger(currValue)) {
						currValue = ((Number) currValue).doubleValue() + ((Number) delta).doubleValue();
					} else {
						currValue = ((Number) currValue).longValue() + ((Number) delta).longValue();
					}
					return currValue;
				default:
					return currValue;
			}
		} catch (NumberFormatException e) {
			return  currValue;
		}
	}


	private boolean isInteger(Object val) {

		if( val instanceof Integer || val instanceof  Long || val instanceof  Boolean ){
			return true;
		}else if(val instanceof Float || val instanceof  Double){
			return false;
		}else{
			throw new NumberFormatException();
		}

	}
}
