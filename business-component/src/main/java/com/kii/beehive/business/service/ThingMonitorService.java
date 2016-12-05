package com.kii.beehive.business.service;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.ruleengine.TriggerCreator;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.service.ThingStatusMonitorDao;
import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.target.CallBusinessFunction;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component("thingMonitorService")
public class ThingMonitorService {

	@Autowired
	private UserNoticeDao noticificationDao;

	@Autowired
	private TriggerCreator creator;
	
	@Autowired
	private ThingStatusMonitorDao  monitorDao;

	@Autowired
	private GlobalThingSpringDao thingDao;


	@Transactional
	public void addNotifiction(String monitorID,String thingID,ThingStatus status){
		ThingStatusMonitor monitor=monitorDao.getObjectByID(monitorID);


		UserNotice notice=new UserNotice();
		notice.setFrom(monitor.getName());
		notice.setData(status.getFields());
		notice.setReaded(false);

		ThingIDTools.ThingIDCombine ids=ThingIDTools.splitFullKiiThingID(thingID);

		GlobalThingInfo th=thingDao.getThingByFullKiiThingID(ids.kiiAppID,ids.kiiThingID);

		
		notice.setTitle(th.getVendorThingID());
		notice.setType(UserNotice.MsgType.ThingStatus);
		
		monitor.getNoticeList().forEach(id->{
			notice.setUserID(id);
			noticificationDao.insert(notice);
		});

	}

	public void addMonitor(ThingStatusMonitor  monitor){


		
		

		

	}
	
	public void removeMonitor(String id){
		
	}
	
	public void enableMonitor(String id){
		
	}
	
	public void disableMonitor(String id){
		
	}
	
	public void updateMonitor(ThingStatusMonitor monitor){
		
		
		
	}
	
	
	private MultipleSrcTriggerRecord  getTrigger(ThingStatusMonitor monitor){
		
		MultipleSrcTriggerRecord record=new MultipleSrcTriggerRecord();
		
		GroupSummarySource source=new GroupSummarySource();
		source.setFunction(SummaryFunctionType.collect);
		TagSelector src=new TagSelector();
		src.setThingList(new ArrayList<>(monitor.getThings()));
		source.setSource(src);
		
		Express express=new Express();
		express.setExpress(monitor.getExpress());
		express.setCondition(monitor.getCondition());
		
		source.setExpress(express);
		
		record.addSource("one", source);
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		predicate.setExpress("$p{comm}.contains($curr.currThing)");
		record.setPredicate(predicate);
		
		
		CallBusinessFunction target  =new CallBusinessFunction();
		target.setBeanName("thingMonitorService");
		target.setFunctionName("addNotifiction");
		target.setParamArrays("monitorID","currThing","thingStatus");
		
		record.addTarget(target);
		
		record.addTargetParam("currThing","$curr.currThing");
		record.addTargetParam("monitorID",monitor.getId());
		record.addTargetParam("thingStatus","$curr.status");
		
		
		return record;
	}



}
