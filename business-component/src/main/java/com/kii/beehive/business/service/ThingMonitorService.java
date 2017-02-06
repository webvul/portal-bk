package com.kii.beehive.business.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.entity.MonitorQuery;
import com.kii.beehive.business.entity.ThingStatusNoticeEntry;
import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CommLangsUtils;
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
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.ThingCollectSource;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.task.CallBusinessFunction;
import com.kii.extension.sdk.service.AbstractDataAccess;
import com.kii.extension.tools.AdditionFieldType;

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
	
	private Logger log= LoggerFactory.getLogger(ThingMonitorService.class);

	@Transactional
	public void addNotifiction(String monitorID,Map<String,Map<String,Object>> statusCols,Set<String> currMatcherIDs,Set<String> history){
		ThingStatusMonitor monitor=monitorDao.getObjectByID(monitorID);


		if(monitor.getStatus()!= ThingStatusMonitor.MonitorStatus.enable){
			return;
		}
		
//
		List<UserNotice>  noticeList=new ArrayList<>();
		
		
		Set<String> removed=new HashSet<>(history);
		Set<String> added=new HashSet<>(currMatcherIDs);
		
		removed.removeAll(currMatcherIDs);
		
		added.removeAll(history);
		
		
		removed.forEach(th->{
			noticeList.add(getUserNotice(monitor,th, NoticeActionType.ThingMonitorType.true2false,statusCols.get(th),currMatcherIDs));
		});
		
		
		added.forEach(th->{
			noticeList.add(getUserNotice(monitor,th, NoticeActionType.ThingMonitorType.false2true,statusCols.get(th),currMatcherIDs));
		});
		
		
		List<UserNotice> noticesPreUser=new ArrayList<>();
		monitor.getNoticeList().forEach(id->{
			
			noticeList.forEach((n)->{
				
				n.setUserID(id);
				noticesPreUser.add(n);
				queue.addNotice(n);
			});
	
		});
		
		noticificationDao.batchInsert(noticesPreUser);
	}
	
	private UserNotice getUserNotice(ThingStatusMonitor monitor, String businessID, NoticeActionType.ThingMonitorType sign, Map<String,Object> status, Set<String> currMatcherIDs){
		
		UserNotice notice=new UserNotice();
		
		notice.setFrom(monitor.getName());
		
		
		
		notice.setActionType(sign.name());
		notice.setReaded(false);
		notice.setCreateTime(new Date());
		notice.setType(UserNotice.MsgType.ThingStatus);
		notice.setMsgInText(monitor.getDescription());
		
		Long thingID=Long.parseLong(BusinessDataObject.getInstance(businessID).getBusinessObjID());
		
		GlobalThingInfo th=thingDao.findByID(thingID);
		notice.setTitle(th.getVendorThingID());
		
		monitor.getAdditions().forEach( (k,v)->{
			int idx= AdditionFieldType.getIndex(k);
			
			if(k.startsWith(AdditionFieldType.Str.name())){
				notice.getAdditionString().put(idx,(String)v);
			}else if(k.startsWith(AdditionFieldType.Int.name())){
				notice.getAdditionInteger().put(idx,(Integer)v);
			}
			
		});
		ThingStatusNoticeEntry entry=new ThingStatusNoticeEntry();
		entry.setActionType(sign);
		entry.setCurrThing(th.getVendorThingID());
		entry.setCurrThingInThID(th.getId());
		entry.setCurrStatus(status);
		entry.setMonitor(monitor);
		
		
		List<Long> thingIDs=currMatcherIDs.stream().map(id-> Long.parseLong(BusinessDataObject.getInstance(businessID).getBusinessObjID())).collect(Collectors.toList());
		
		List<GlobalThingInfo>  thList=thingDao.findByIDs(thingIDs);
		
		Set<String> matcher=thList.stream().map(GlobalThingInfo::getVendorThingID).collect(Collectors.toSet());
		entry.setCurrMatchers(matcher);
		entry.setCurrMatchersInThID(thList.stream().map(GlobalThingInfo::getId).collect(Collectors.toSet()));
		
		String json= "{}";
		try {
			json = mapper.writeValueAsString(entry);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		
		notice.setData(json);
		
		return notice;
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
		
		if(StringUtils.isNotBlank(monitor.getExpress())||
				monitor.getCondition()!=null) {
			TriggerRecord trigger = getTrigger(monitor, ids);
			
			try {
				String triggerID = creator.createTrigger(trigger).getTriggerID();
				
				monitor.setRelationTriggerID(triggerID);
				
				monitorDao.updateEntity(Collections.singletonMap("relationTriggerID", triggerID), monitorID);
			} catch (TriggerCreateException ex) {
				monitorDao.removeEntity(monitorID);
				throw ex;
			}
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
			if(StringUtils.isNotBlank(monitor.getRelationTriggerID())) {
				creator.enableTrigger(monitor.getRelationTriggerID());
			}
			monitorDao.updateEntity(Collections.singletonMap("status", ThingStatusMonitor.MonitorStatus.enable), id);
		}else{
			throw new InvalidEntryStatusException("ThingStatusMonitor","status",monitor.getStatus().name());
		}
	}
	
	public void disableMonitor(String id){
		ThingStatusMonitor monitor=monitorDao.getObjectByID(id);
		
		if(monitor.getStatus()== ThingStatusMonitor.MonitorStatus.enable) {
			
			if(StringUtils.isNotBlank(monitor.getRelationTriggerID())) {
				creator.disableTrigger(monitor.getRelationTriggerID());
			}
			monitorDao.updateEntity(Collections.singletonMap("status", ThingStatusMonitor.MonitorStatus.disable), id);
		}else{
			throw new InvalidEntryStatusException("ThingStatusMonitor","status",monitor.getStatus().name());
		}
	}
	
	
	
	@Transactional
	public void updateMonitor(ThingStatusMonitor monitor){
		
		
		ThingStatusMonitor oldMonitor=monitorDao.getObjectByID(monitor.getId());
		if(oldMonitor.getStatus()== ThingStatusMonitor.MonitorStatus.deleted){
			throw new InvalidEntryStatusException("ThingStatusMonitor","status",ThingStatusMonitor.MonitorStatus.deleted.name());
		}
		if((!monitor.getVendorThingIDList().isEmpty()&&!oldMonitor.getVendorThingIDList().equals(monitor.getVendorThingIDList()))||
				(monitor.getExpress()!=null &&!CommLangsUtils.safeEquals(oldMonitor.getExpress(),monitor.getExpress()) )||
				(monitor.getCondition()!=null && !CommLangsUtils.safeEquals(oldMonitor.getCondition(),monitor.getCondition()))) {
			
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
			if(StringUtils.isBlank(oldMonitor.getRelationTriggerID())){
				
				String relationTriggerID=creator.createTrigger(newTrigger).getTriggerID();
				monitor.setRelationTriggerID(relationTriggerID);
				
			}else {
				newTrigger.setTriggerID(oldMonitor.getRelationTriggerID());
				creator.updateTrigger(newTrigger);
			}
		}
		
		if(monitor.getVendorThingIDList().isEmpty()){
			monitor.setVendorThingIDList(oldMonitor.getVendorThingIDList());
		}
		if(monitor.getNoticeList().isEmpty()){
			monitor.setNoticeList(oldMonitor.getNoticeList());
		}
		monitor.updateThingIDs(oldMonitor.getVendorThingIDList());
		
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
		
		ThingCollectSource src=new ThingCollectSource();
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
		target.setParamArrays("monitorID","thingStatusCol","matcher","history");
		
		
		record.addTarget(target);
		
//		record.addTargetParam("currThings","$e{sys.curr.currThings}");
		record.addTargetParam("monitorID","'"+monitor.getId()+"'");
		record.addTargetParam("thingStatusCol","$e{runtime.currStatusCol}");
		record.addTargetParam("matcher","$p:c{one}");
		record.addTargetParam("history","$h:c{one}");
		
		
		record.setRecordStatus(TriggerRecord.StatusType.enable);
		
		return record;
	}
	
	public ThingStatusMonitor getMonitor(String monitorID) {
		
		return monitorDao.getObjectByID(monitorID);
	}
	
	
	public List<ThingStatusMonitor> queryMonitor(MonitorQuery query, AbstractDataAccess.KiiBucketPager pager){
		
		
		return monitorDao.getMonitorsByQuery(query.generQuery(),pager);
		
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
