package com.kii.beehive.business.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

	public void updateThingGroup(List<GlobalThingInfo>  thingList,String triggerID){

		Map<String,Boolean> updateMap=new HashMap<>();


		TriggerRuntimeState state=statusDao.getObjectByID(triggerID);

		Map<String,Boolean> stateMap=state.getMemberStatusMap();

		Set<String> oldIDs=stateMap.keySet();
		Set<String> thingIDs=thingList.stream().map(thing->thing.getKiiThingID()).collect(Collectors.toSet());

		oldIDs.removeAll(thingIDs);

		oldIDs.forEach(id->{
			updateMap.put(id,null);
		});

		statusDao.updateEntityWithVersion(updateMap,triggerID,state.getVersion());

		thingList.removeIf(thing-> stateMap.containsKey(thing.getKiiThingID()));

		initGroupState(thingList);
	}

}
