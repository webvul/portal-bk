package com.kii.beehive.business.service;


import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.entity.ThingStatusNoticeEntry;
import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CommLangsUtils;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.exception.InvalidEntryStatusException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.NoticeActionType;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.service.ThingStatusMonitorDao;
import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.extension.ruleengine.TriggerCreateException;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.target.CallBusinessFunction;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component("thingMonitorService")
public class ThingMonitorService {
	
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private UserNoticeDao noticificationDao;

	@Autowired
	private TriggerManager creator;
	
	@Autowired
	private ThingStatusMonitorDao  monitorDao;
	
	@Autowired
	private GlobalThingSpringDao thingDao;

	@Autowired
	private NoticeMsgQueue queue;

	@Transactional
	public void addNotifiction(String monitorID, String thingID, Map<String,Object> status, Boolean sign,Set<String> currMatcher){
		ThingStatusMonitor monitor=monitorDao.getObjectByID(monitorID);


		UserNotice notice=new UserNotice();
		
		notice.setFrom(monitor.getName());
		

		notice.setActionType(sign?NoticeActionType.ThingMonitorType.false2true.name():NoticeActionType.ThingMonitorType.true2false.name());
		notice.setReaded(false);
		notice.setCreateTime(new Date());
		notice.setType(UserNotice.MsgType.ThingStatus);
		
		ThingIDTools.ThingIDCombine ids=ThingIDTools.splitFullKiiThingID(thingID);

		GlobalThingInfo th=thingDao.getThingByFullKiiThingID(ids.kiiAppID,ids.kiiThingID);
		notice.setTitle(th.getVendorThingID());
		
		ThingStatusNoticeEntry entry=new ThingStatusNoticeEntry();
		entry.setActionType(sign?NoticeActionType.ThingMonitorType.false2true:NoticeActionType.ThingMonitorType.true2false);
		entry.setCurrThing(th.getVendorThingID());
		entry.setCurrStatus(status);
		entry.setMonitorID(monitorID);
		
		Set<String> matcher=thingDao.getThingListByFullKiiThingIDs(currMatcher).stream().map(GlobalThingInfo::getVendorThingID).collect(Collectors.toSet());
		entry.setCurrMatchers(matcher);
		
		String json= "{}";
		try {
			json = mapper.writeValueAsString(entry);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		notice.setData(json);
		
		monitor.getNoticeList().forEach(id->{
			notice.setUserID(id);
			noticificationDao.insert(notice);
			
			queue.addNotice(notice);
		});
		
	}
	
	

	public String addMonitor(ThingStatusMonitor  monitor){
		
		
		monitor.getNoticeList().add(AuthInfoStore.getUserID());
		
		monitor.setStatus(ThingStatusMonitor.MonitorStatus.enable);
		
		String monitorID=monitorDao.addEntity(monitor).getObjectID();
		
		List<Long> ids=thingDao.getThingsByVendorThings(monitor.getVendorThingIDList()).stream().map(GlobalThingInfo::getId).collect(Collectors.toList());
		if(ids.size()<monitor.getVendorThingIDList().size()){
			
			throw new UnauthorizedException(UnauthorizedException.NOT_THING_CREATOR,"user",AuthInfoStore.getUserIDStr());
		}
		monitor.setId(monitorID);
		TriggerRecord trigger=getTrigger(monitor,ids);
		
		try {
			String triggerID = creator.createTrigger(trigger).getTriggerID();
			
			monitor.setRelationTriggerID(triggerID);
			
			monitorDao.updateEntity(Collections.singletonMap("relationTriggerID", triggerID), monitorID);
		}catch(TriggerCreateException ex){
			monitorDao.removeEntity(monitorID);
			throw ex;
		}
		
		return monitorID;
	}
	
	public void removeMonitor(String id){
		
		ThingStatusMonitor monitor=monitorDao.getObjectByID(id);
		
		if(monitor.getStatus()!= ThingStatusMonitor.MonitorStatus.deleted) {
			
			creator.deleteTrigger(monitor.getRelationTriggerID());
			
			monitorDao.updateEntity(Collections.singletonMap("status", ThingStatusMonitor.MonitorStatus.deleted), id);
		}else{
			throw new InvalidEntryStatusException("ThingStatusMonitor","status",monitor.getStatus().name());
			
		}
	}
	
	public void enableMonitor(String id){
		
		ThingStatusMonitor monitor=monitorDao.getObjectByID(id);
		if(monitor.getStatus()== ThingStatusMonitor.MonitorStatus.disable) {
			creator.enableTrigger(monitor.getRelationTriggerID());
			monitorDao.updateEntity(Collections.singletonMap("status", ThingStatusMonitor.MonitorStatus.enable), id);
		}else{
			throw new InvalidEntryStatusException("ThingStatusMonitor","status",monitor.getStatus().name());
		}
	}
	
	public void disableMonitor(String id){
		ThingStatusMonitor monitor=monitorDao.getObjectByID(id);
		
		if(monitor.getStatus()== ThingStatusMonitor.MonitorStatus.enable) {
			
			creator.disableTrigger(monitor.getRelationTriggerID());
			
			monitorDao.updateEntity(Collections.singletonMap("status", ThingStatusMonitor.MonitorStatus.disable), id);
		}else{
			throw new InvalidEntryStatusException("ThingStatusMonitor","status",monitor.getStatus().name());
		}
	}
	
	
	
	@Transactional
	public void updateMonitor(ThingStatusMonitor monitor){
		
		
		ThingStatusMonitor oldMonitor=monitorDao.getObjectByID(monitor.getId());
		
		if(!oldMonitor.getVendorThingIDs().equals(monitor.getVendorThingIDs())||
				!CommLangsUtils.safeEquals(oldMonitor.getExpress(),monitor.getExpress())||
				!CommLangsUtils.safeEquals(oldMonitor.getCondition(),monitor.getCondition())) {
			
			Collection<String> things=monitor.getVendorThingIDList();
			if(things.isEmpty()){
				things=oldMonitor.getVendorThingIDList();
			}
			List<Long> ids=thingDao.getThingsByVendorThings(things).stream().map(GlobalThingInfo::getId).collect(Collectors.toList());
			
			if(monitor.getExpress()==null&&monitor.getCondition()==null){
				monitor.setExpress(oldMonitor.getExpress());
				monitor.setCondition(oldMonitor.getCondition());
			}
			
			TriggerRecord newTrigger=getTrigger(monitor,ids);
			newTrigger.setTriggerID(oldMonitor.getRelationTriggerID());
			creator.updateTrigger(newTrigger);
			
		}
		
		monitorDao.updateEntity(monitor,oldMonitor.getId());
	}
	
	
	
	private MultipleSrcTriggerRecord  getTrigger(ThingStatusMonitor monitor,List<Long> ids){
		
		MultipleSrcTriggerRecord record=new MultipleSrcTriggerRecord();
		
		record.setUsedByWho(TriggerRecord.UsedByType.User_monitor);
		
		record.setUserID(-999l);

		record.fillCreator(monitor.getId());
		
		record.setName("mon"+monitor.getId());
		GroupSummarySource source=new GroupSummarySource();
		source.setFunction(SummaryFunctionType.objCol);
		TagSelector src=new TagSelector();
		
		src.setThingList(ids);
		source.setSource(src);
		
		Express express=new Express();
		express.setExpress(monitor.getExpress());
		express.setCondition(monitor.getCondition());
		
		source.setExpress(express);
		
		record.addSource("one", source);
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		predicate.setExpress("$p:c{one}!=$h:c{one}");
		predicate.setTriggersWhen(WhenType.CONDITION_TRUE);
		record.setPredicate(predicate);
		
		
		CallBusinessFunction target  =new CallBusinessFunction();
		target.setBeanName("thingMonitorService");
		target.setFunctionName("addNotifiction");
		target.setParamArrays("monitorID","currThing","thingStatus","sign","matcher");
		
		
		record.addTarget(target);
		
		record.addTargetParam("currThing","$e{sys.curr.currThing}");
		record.addTargetParam("monitorID","'"+monitor.getId()+"'");
		record.addTargetParam("thingStatus","$e{runtime.currStatus}");
		record.addTargetParam("sign","$p:c{one}.contains($e{sys.curr.currThing})");
		record.addTargetParam("matcher","$p:c{one}");
		
		record.setRecordStatus(TriggerRecord.StatusType.enable);
		
		return record;
	}
	
	public ThingStatusMonitor getMonitor(String monitorID) {
		
		return monitorDao.getObjectByID(monitorID);
	}
	
	
	public List<ThingStatusMonitor> queryMonitor(ThingStatusMonitorDao.MonitorQuery query,AbstractDataAccess.KiiBucketPager pager){
		
		
		return monitorDao.getMonitorsByQuery(query,pager);
		
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
