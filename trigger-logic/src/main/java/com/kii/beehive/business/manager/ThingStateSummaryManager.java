package com.kii.beehive.business.manager;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.business.service.ThingTagService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.SummaryExpress;
import com.kii.beehive.portal.store.entity.trigger.SummaryFunctionType;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.Predicate;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.entity.thingif.TriggerTarget;

@Component
public class ThingStateSummaryManager {


	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private ThingTagService  thingTagService;

	@Autowired
	private TriggerRecordDao  triggerDao;

	@Autowired
	private KiicloudEventListenerService listenerService;

	@Autowired
	private AppInfoManager appInfoManager;



//	@Autowired
//	private AppInfoDao appDao;


	public void initStateSummary(SummaryTriggerRecord record){


//		KiiAppInfo appInfo=appDao.getMasterAppInfo();

		OnBoardingParam param=new OnBoardingParam();
		param.setThingPassword("demo");
//		param.setUserID(appInfo.getDefaultThingOwnerID());

		OnBoardingResult result=thingIFService.onBoarding(param,"master");

		String summaryID=result.getThingID();
		record.setSummaryThingID(summaryID);

		TriggerSource  source=record.getSource();
		List<GlobalThingInfo> things=thingTagService.getThingInfos(source.getSelector());

		List<String> thingIDs=things.stream().map(thing->thing.getFullKiiThingID()).collect(Collectors.toList());

		String listenerID=listenerService.addThingStatusListener(thingIDs,summaryID);
		record.setListenerID(listenerID);

		if(!source.getSelector().getTagList().isEmpty()) {

			listenerService.addSummaryChangeListener(source.getSelector().getTagList(), summaryID);

		}

		String triggerID=triggerDao.addKiiEntity(record);


		registTrigger(summaryID,triggerID,record.getPerdicate());

		refreshThingState(thingIDs);

	}

	private void registTrigger(String summaryID, String triggerID, Predicate perdicate){

		String masterAppID=appInfoManager.getMasterAppID();


		ThingTrigger triggerInfo=new ThingTrigger();
		triggerInfo.setTarget(TriggerTarget.SERVER_CODE);

		triggerInfo.setPredicate(perdicate);

		String fullThingID= ThingIDTools.joinFullKiiThingID(summaryID,masterAppID);


		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint(EndPointNameConstant.SummaryTriggerEndPoint);
		serviceCode.addParameter("thingID",summaryID);
		serviceCode.addParameter("triggerID",triggerID);

		serviceCode.setTargetAppID(masterAppID);
		serviceCode.setExecutorAccessToken(appInfoManager.getDefaultOwer(masterAppID).getMasterAuthToken());

		triggerInfo.setServiceCode(serviceCode);

		thingIFService.createTrigger(fullThingID,triggerInfo);

		return;
	}



	private void refreshThingState(List<String> thingList){

		thingList.forEach(thingID->{


			ThingStatus status=thingIFService.getStatus(thingID);

			thingIFService.putStatus(thingID,status);

		});
	}

	public void onTagChanged(String triggerID){


		SummaryTriggerRecord  record= (SummaryTriggerRecord) triggerDao.getObjectByID(triggerID);

		TagSelector source=record.getSource().getSelector();

		List<GlobalThingInfo> thingList=thingTagService.queryThingByTagExpress(source.isAndExpress(),source.getTagList());

		List<String> thingIDList=thingList.stream().map(thing->thing.getFullKiiThingID()).collect(Collectors.toList());

		listenerService.updateThingStatusListener(thingIDList,record.getListenerID());

		refreshThingState(thingIDList);
	}


	public void computeStateSummary(String  summaryID,ThingStatus status){



		String thingID=summaryID;

		ThingStatus summaryState=thingIFService.getStatus(thingID);

		String triggerID= (String) summaryState.getField("_triggerID");

		SummaryTriggerRecord  trigger= (SummaryTriggerRecord) triggerDao.getObjectByID(triggerID);

		List<SummaryExpress> expressList=trigger.getSummaryExpress();

		expressList.forEach(express->{

			String summaryField=express.getSummaryAlias();

			String fieldName=express.getStateName();

			Object summary=summaryState.getField(summaryField);

			Object delta=status.getField(fieldName);


			try {
				switch (express.getFunction()) {
					case Count:
						summaryState.setField(summaryField, ((Long) summary) + 1);
						break;
					case Max:
						if (!isInteger(delta) || !isInteger(summary)) {
							boolean sign = ((Number) summary).doubleValue() >= ((Number) delta).doubleValue();
							if (!sign) {
								summary = delta;
							}
						} else {
							summary = Math.max(((Number) summary).longValue(), ((Number) delta).longValue());
						}
						summaryState.setField(summaryField, summary);
						break;
					case Min:
						if (!isInteger(delta) || !isInteger(summary)) {
							boolean sign = ((Number) summary).doubleValue() <= ((Number) delta).doubleValue();
							if (!sign) {
								summary = delta;
							}
						} else {
							summary = Math.min(((Number) summary).longValue(), ((Number) delta).longValue());
						}
						summaryState.setField(summaryField, summary);

						break;
					case Sum:
						if (!isInteger(delta) || !isInteger(summary)) {
							summary = ((Number) summary).doubleValue() + ((Number) delta).doubleValue();
						} else {
							summary = ((Number) summary).longValue() + ((Number) delta).longValue();
						}
						summaryState.setField(summaryField, summary);

						break;
				}
			}catch(NumberFormatException e){
				summaryState.setField(summaryField,summary);
			}

		});

		expressList.stream().filter(expresss-> expresss.getFunction()!=SummaryFunctionType.Avg).forEach(express->{

			String sumField=express.getSumField();
			String countField=express.getCountField();

			Number sum= (Number) summaryState.getField(sumField);

			Number count= (Number) summaryState.getField(countField);

			double avg=sum.doubleValue()/count.longValue();

			summaryState.setField(express.getSummaryAlias(),avg);
		});


		thingIFService.putStatus(thingID,summaryState);
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
