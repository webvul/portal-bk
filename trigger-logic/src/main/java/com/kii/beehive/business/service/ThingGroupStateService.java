package com.kii.beehive.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerStatusDao;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.service.ThingIFService;

@Component
public class ThingGroupStateService {


	@Autowired
	private ThingIFService thingService;

	@Autowired
	private TriggerStatusDao statusDao;


	private void initGroupState(List<GlobalThingInfo> thingList){


		thingList.stream().map(GlobalThingInfo::getKiiThingID).forEach(thingID->{

			ThingStatus status=thingService.getStatus(thingID);

			thingService.putStatus(thingID,status);

		});

	}



	public void createThingGroup(List<GlobalThingInfo>  thingList, TriggerWhen when, String triggerID, TriggerSource source){


		TriggerRuntimeState state=new TriggerRuntimeState();
		state.setId(triggerID);
		state.setPolicy(source.getGroupPolicy());
		state.setCriticalNumber(source.getCriticalNumber());
		state.setWhenType(when);

		statusDao.addEntity(state,triggerID);

		initGroupState(thingList);

	}

}
