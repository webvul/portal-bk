package com.kii.beehive.business.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.SummaryExpress;
import com.kii.beehive.portal.store.entity.trigger.SummaryFunctionType;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.service.ThingIFService;

@Component
public class ThingStateSummaryService {


	@Autowired
	private ThingIFService thingIFService;

	@Autowired
	private TriggerRecordDao  triggerDao;



	public void computeStateSummary(String  triggerID,ThingStatus status){

		TriggerRecord  trigger=triggerDao.getObjectByID(triggerID);


		String thingID=null;

		ThingStatus summaryState=thingIFService.getStatus(thingID);

		List<SummaryExpress> expressList=trigger.getSummaryExpress();


		expressList.forEach(express->{

			String fieldName=express.getStateName();

			Object summary=summaryState.getField(fieldName);

			Object delta=status.getField(fieldName);


			try {
				switch (express.getFunction()) {
					case Count:
						summaryState.setField(fieldName, ((Long) summary) + 1);
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
						summaryState.setField(fieldName, summary);
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
						summaryState.setField(fieldName, summary);

						break;
					case Sum:
						if (!isInteger(delta) || !isInteger(summary)) {
							summary = ((Number) summary).doubleValue() + ((Number) delta).doubleValue();
						} else {
							summary = ((Number) summary).longValue() + ((Number) delta).longValue();
						}
						summaryState.setField(fieldName, summary);

						break;
				}
			}catch(NumberFormatException e){
				summaryState.setField(fieldName,summary);
			}

		});

		expressList.stream().filter(expresss-> expresss.getFunction()!=SummaryFunctionType.Avg).forEach(express->{

			String sumField=express.getSumField();
			String countField=express.getCountField();

			Number sum= (Number) summaryState.getField(sumField);

			Number count= (Number) summaryState.getField(countField);

			double avg=sum.doubleValue()/count.longValue();

			summaryState.setField(express.getStateName(),avg);
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
