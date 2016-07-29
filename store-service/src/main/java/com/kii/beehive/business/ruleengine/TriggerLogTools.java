package com.kii.beehive.business.ruleengine;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class TriggerLogTools {


	@Autowired
	private OpLogTools logTool;


	@Autowired
	private TriggerRecordDao dao;


	@Autowired
	private ThingTagManager thingTagService;


	@Async
	public void outputCommandLog(Set<GlobalThingInfo> thingList, TriggerRecord record) {

		thingList.forEach(thing -> {

			//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”fire"+当前triggerID+触发源
			List<String> list = new LinkedList<>();
			list.add(AuthInfoStore.getUserIDStr());
			list.add("trigger");
			list.add(record.getType().name());
			list.add("exec");
			list.add(record.getTriggerID());
			//触发目标
			list.add(thing.getId() + "");

			logTool.write(list);
		});

	}

	@Async
	public void outputCreateLog(TriggerRecord record, String triggerID) {
		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”create"+当前triggerID
		List<String> list = new LinkedList<>();
		list.add(AuthInfoStore.getUserIDStr());
		list.add("trigger");
		list.add(record.getType().name());
		list.add("create");
		list.add(triggerID);
		logTool.write(list);
	}

	@Async
	public void outputDeleteLog(String triggerID) {
		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”delete"+当前triggerID
		List<String> list = new LinkedList<>();
		list.add(AuthInfoStore.getUserIDStr());
		list.add("trigger");
		list.add(" ");
		list.add("delete");
		list.add(triggerID);
		logTool.write(list);
	}

	@Async
	public void outputEnableLog(TriggerRecord record) {
		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”enable"+当前triggerID
		List<String> list = new LinkedList<>();
		list.add(AuthInfoStore.getUserIDStr());
		list.add("trigger");
		list.add(record.getType().name());
		list.add("enable");
		list.add(record.getTriggerID());
		logTool.write(list);
	}

	@Async
	public void outputDisableLog(TriggerRecord record) {
		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”disable"+当前triggerID
		List<String> list = new LinkedList<>();
		list.add(AuthInfoStore.getUserIDStr());
		list.add("trigger");
		list.add(record.getType().name());
		list.add("disable");
		list.add(record.getTriggerID());
		logTool.write(list);
	}

	@Async
	public void outputFireLog(String triggerID) {
		TriggerRecord record = dao.getTriggerRecord(triggerID);
		String triggerType;
		Set<String> thingIDs;

		if (record == null) {
			return;
		}
		if (record instanceof SimpleTriggerRecord) {
			SimpleTriggerRecord simpleTriggerRecord = (SimpleTriggerRecord) record;
			triggerType = simpleTriggerRecord.getType().name();

			thingIDs = new HashSet<>();
			if (simpleTriggerRecord.getSource() != null) {
				thingIDs.add(simpleTriggerRecord.getSource().getThingID() + "");
			}

		} else if (record instanceof GroupTriggerRecord) {

			GroupTriggerRecord groupTriggerRecord = (GroupTriggerRecord) record;
			triggerType = groupTriggerRecord.getType().name();

			thingIDs = thingTagService.getKiiThingIDs(groupTriggerRecord.getSource());

		} else if (record instanceof SummaryTriggerRecord && ((SummaryTriggerRecord) record).getSummarySource() != null) {
			SummaryTriggerRecord summaryTriggerRecord = (SummaryTriggerRecord) record;
			triggerType = summaryTriggerRecord.getType().name();

			thingIDs = new HashSet<>();//thingTagService.getKiiThingIDs(summaryTriggerRecord.getSummarySource().);
		} else {
			thingIDs = new HashSet<>();
			triggerType = "";
		}


		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”fire"+当前triggerID+触发源
		List<String> list = new LinkedList<>();
		list.add(AuthInfoStore.getUserIDStr());
		list.add("trigger");
		list.add(triggerType);
		list.add("fire");
		list.add(triggerID);
		//触发源
		String thingIDStrs = "[";
		for (String thingID : thingIDs) {
			thingIDStrs += thingID + "#";
		}
		if (thingIDStrs.length() > 1) {
			thingIDStrs = thingIDStrs.substring(0, thingIDStrs.length() - 2) + "]";
		} else {
			thingIDStrs = "[]";
		}

		list.add(thingIDStrs);

		logTool.write(list);
	}
}
