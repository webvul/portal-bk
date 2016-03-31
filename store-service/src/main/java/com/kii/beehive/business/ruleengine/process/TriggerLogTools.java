package com.kii.beehive.business.ruleengine.process;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@Component
public class TriggerLogTools {


	@Autowired
	private OpLogTools logTool;


	@Autowired
	private TriggerRecordDao  dao;


	@Autowired
	private ThingTagManager thingTagService;


	@Async
	public void outputLog(String triggerID){
		TriggerRecord record=dao.getTriggerRecord(triggerID);
		String triggerType ;
		Set<String> thingIDs;

		if(record == null){
			return;
		}
		if(record instanceof SimpleTriggerRecord){
			SimpleTriggerRecord simpleTriggerRecord = (SimpleTriggerRecord)record;
			triggerType = simpleTriggerRecord.getType().name();

			thingIDs = new HashSet<>();
			thingIDs.add(simpleTriggerRecord.getSource().getThingID()+"");
		}else if(record instanceof GroupTriggerRecord){
			GroupTriggerRecord groupTriggerRecord = (GroupTriggerRecord)record;
			triggerType = groupTriggerRecord.getType().name();

			thingIDs = thingTagService.getKiiThingIDs(groupTriggerRecord.getSource().getSelector());


		}else{
			SummaryTriggerRecord summaryTriggerRecord = (SummaryTriggerRecord)record;
			triggerType = summaryTriggerRecord.getType().name();

			thingIDs = new HashSet<>();//thingTagService.getKiiThingIDs(summaryTriggerRecord.getSummarySource().);
		}


		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”fire"+当前triggerID+触发源
		List<String> list=new LinkedList<>();
		list.add(AuthInfoStore.getUserID());
		list.add("trigger");
		list.add(triggerType);
		list.add("fire");
		list.add(triggerID);
		//触发源
		String thingIDStrs = "[";
		for (String thingID : thingIDs){
			thingIDStrs += thingID+"#";
		}
		if(thingIDStrs.length()>1){
			thingIDStrs = thingIDStrs.substring(0,thingIDStrs.length()-2)+"]";
		}else{
			thingIDStrs="[]";
		}

		list.add(thingIDStrs);

		logTool.write(list);
	}
}
