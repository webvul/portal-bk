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


	public void initStateSummary(SummaryTriggerRecord record){

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

		thingList.forEach(thingID->{


			ThingStatus status=thingIFService.getStatus(thingID);

			updateState(groupName, status, expressList, entry);
		});

		return entry;
	}

	public void onTagChanged(String triggerID,String groupName){


		SummaryTriggerRecord  record= (SummaryTriggerRecord) triggerDao.getObjectByID(triggerID);

		SummarySource source=record.getSummarySource().get(groupName);

		TagSelector tagSelector=source.getSource().getSelector();
		List<GlobalThingInfo> thingList=thingTagService.getThingInfos(tagSelector);
		List<String> thingIDList=thingList.stream().map(thing->thing.getFullKiiThingID()).collect(Collectors.toList());

		SummaryTriggerRuntimeState state=statusDao.getSummaryRuntimeState(triggerID);

		listenerService.updateThingStatusListener(thingIDList,state.getListeners().get(groupName).getTagListenerID());

		refreshThingState(thingIDList,groupName,record.getSummarySource().get(groupName).getExpressList(),state.getListeners().get(groupName));

	}


	public void computeStateSummary(String  triggerID,String groupName,ThingStatus status){

		SummaryTriggerRecord  trigger= (SummaryTriggerRecord) triggerDao.getObjectByID(triggerID);

		for(int i=0;i<5;i++) {

			SummaryTriggerRuntimeState state=statusDao.getSummaryRuntimeState(triggerID);
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

			String fieldName = express.getStateName();

			String summaryField = groupName + "." + express.getSummaryAlias();



			Object delta = status.getField(fieldName);
			Object currValue = summary.get(summaryField);

			Object newValue=computeSummary(delta, express.getFunction(), currValue);

			summary.put(summaryField,newValue);
		}


		expressList.stream()
				.filter(expresss-> expresss.getFunction()!= SummaryFunctionType.Avg)
				.forEach(express->{

			String sumField=express.getSumField();
			String countField=express.getCountField();

			Number sum= (Number) summary.get(sumField);

			Number count= (Number) summary.get(countField);

			double avg=sum.doubleValue()/count.longValue();

			summary.put(express.getSummaryAlias(),avg);
		});

		entry.setSummary(summary);
		return;
	}

	private Object computeSummary(Object delta, SummaryFunctionType type, Object currValue) {

		try {
			switch (type) {
				case Count:
					return ((Long) currValue) + 1;
				case Max:
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