package com.kii.beehive.business.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.NoticeActionType;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.service.ThingStatusMonitorDao;
import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.target.CallBusinessFunction;

@Component("thingMonitorService")
public class ThingMonitorService {

	@Autowired
	private UserNoticeDao noticificationDao;

	@Autowired
	private TriggerManager creator;
	
	@Autowired
	private ThingStatusMonitorDao  monitorDao;
	
	@Autowired
	private GlobalThingSpringDao thingTagService;

	@Autowired
	private NoticeMsgQueue queue;

	@Transactional
	public void addNotifiction(String monitorID, String thingID, Map<String,Object> status, Boolean sign){
		ThingStatusMonitor monitor=monitorDao.getObjectByID(monitorID);


		UserNotice notice=new UserNotice();
		
		notice.setFrom(monitor.getName());
		notice.setData(status);
		notice.setActionType(sign?NoticeActionType.ThingMonitorType.false2true.name():NoticeActionType.ThingMonitorType.true2false.name());
		notice.setReaded(false);
		notice.setCreateTime(new Date());
		notice.setType(UserNotice.MsgType.ThingStatus);
		
		ThingIDTools.ThingIDCombine ids=ThingIDTools.splitFullKiiThingID(thingID);

		GlobalThingInfo th=thingTagService.getThingByFullKiiThingID(ids.kiiAppID,ids.kiiThingID);
		notice.setTitle(th.getVendorThingID());
		
		monitor.getNoticeList().forEach(id->{
			notice.setUserID(id);
			noticificationDao.insert(notice);
			
			queue.addNotice(notice);
		});
		
	}

	public void addMonitor(ThingStatusMonitor  monitor){


		TriggerRecord trigger=getTrigger(monitor);
		
		String triggerID=creator.createTrigger(trigger).getTriggerID();
		
		monitor.setRelationTriggerID(triggerID);
		
		monitorDao.addEntity(monitor);

	}
	
	public void removeMonitor(String id){
		
		ThingStatusMonitor monitor=monitorDao.getObjectByID(id);
		
		creator.deleteTrigger(monitor.getRelationTriggerID());
		
		monitorDao.updateEntity(Collections.singletonMap("status", ThingStatusMonitor.MonitorStatus.deleted),id);
	}
	
	public void enableMonitor(String id){
		
		ThingStatusMonitor monitor=monitorDao.getObjectByID(id);
		
		creator.enableTrigger(monitor.getRelationTriggerID());
		
		monitorDao.updateEntity(Collections.singletonMap("status", ThingStatusMonitor.MonitorStatus.enable),id);
		
		
	}
	
	public void disableMonitor(String id){
		ThingStatusMonitor monitor=monitorDao.getObjectByID(id);
		
		creator.disableTrigger(monitor.getRelationTriggerID());
		
		monitorDao.updateEntity(Collections.singletonMap("status", ThingStatusMonitor.MonitorStatus.disable),id);
		
	}
	
	public void updateMonitor(ThingStatusMonitor monitor){
		
		
		ThingStatusMonitor oldMonitor=monitorDao.getObjectByID(monitor.getId());
		
		if(!oldMonitor.getThings().equals(monitor.getThings())||
			!oldMonitor.getExpress().equals(monitor.getExpress())||
			!oldMonitor.getCondition().equals(monitor.getCondition())) {
			
			TriggerRecord newTrigger=getTrigger(monitor);
			newTrigger.setTriggerID(oldMonitor.getRelationTriggerID());
			creator.updateTrigger(newTrigger);
			
		}
		
		monitorDao.updateEntity(monitor,oldMonitor.getId());
	}
	
	
	private MultipleSrcTriggerRecord  getTrigger(ThingStatusMonitor monitor){
		
		MultipleSrcTriggerRecord record=new MultipleSrcTriggerRecord();
		
		record.setUsedByWho(TriggerRecord.UsedByType.Sys_monitor);
		
		record.setName("mon"+monitor.getName());
		GroupSummarySource source=new GroupSummarySource();
		source.setFunction(SummaryFunctionType.objCol);
		TagSelector src=new TagSelector();
		src.setThingList(new ArrayList<>(monitor.getThings()));
		source.setSource(src);
		
		Express express=new Express();
		express.setExpress(monitor.getExpress());
		express.setCondition(monitor.getCondition());
		
		source.setExpress(express);
		
		record.addSource("one", source);
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		predicate.setExpress("$p{one}!=$h{one}");
		record.setPredicate(predicate);
		
		
		CallBusinessFunction target  =new CallBusinessFunction();
		target.setBeanName("thingMonitorService");
		target.setFunctionName("addNotifiction");
		target.setParamArrays("monitorID","currThing","thingStatus","sign");
		
		record.addTarget(target);
		
		record.addTargetParam("currThing","$e{sys.curr.currThing}");
		record.addTargetParam("monitorID",monitor.getId());
		record.addTargetParam("thingStatus","$e{runtime.currStatus}");
		record.addTargetParam("sign","$p:c{one}.contains($e{sys.curr.currThing})");
		
		return record;
	}
	
	public ThingStatusMonitor getMonitor(String monitorID) {
		
		return monitorDao.getObjectByID(monitorID);
	}

/*
{

  "type": "Multiple",
  "summarySource": {
    "one": {
      "stateName": "",
      "function": "objCol",
      "source": {
        "thingList": [
          0,1,2,3
        ]
      },
      "express":"$p{foo}>$p{bar}",
      "type": "summary"
    }
  },
  "userID": 0,
  "targets": [],
  "predicate": {

    "triggersWhen": "CONDITION_TRUE",
    "express": "  $p{one}!=$h{one} "
  },
  "recordStatus": "enable",
  "targetParamList": [
    {
      "name": "currThing",
      "express": "$e{sys.curr.currThing}"
    },
    {
      "name": "monitorID",
      "express": "'aaaaaaaa'"
    },
    {
      "name": "thingStatus",
      "express": "$e{sys.curr.status}"
    },
    {
      "name":"sign",
      "express":"$p:c{comm}.contains($e{sys.curr.currThing})"
    }
  ],
  "name": "test"
}
 */

}
