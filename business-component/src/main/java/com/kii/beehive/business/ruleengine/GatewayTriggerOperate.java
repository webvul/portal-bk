package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.GatewaySummarySource;
import com.kii.extension.ruleengine.store.trigger.GatewayTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.condition.AndLogic;
import com.kii.extension.ruleengine.store.trigger.condition.Equal;
import com.kii.extension.ruleengine.store.trigger.condition.NotLogic;
import com.kii.extension.ruleengine.store.trigger.condition.OrLogic;
import com.kii.extension.ruleengine.store.trigger.groups.SummarySource;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThingInGW;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingOfKiiCloud;
import com.kii.extension.sdk.query.BucketClause;
import com.kii.extension.sdk.query.QueryParam;


@Component
public class GatewayTriggerOperate {
	
	private static final Logger log = LoggerFactory.getLogger(GatewayTriggerOperate.class);

	@Value("${gateway.resend.fullthingid}")
	private String gatewayFullThingID;
	@Value("${gateway.resend.before.second}")
	private int gatewayBeforeSecond;

	@Autowired
	private AppInfoManager appInfoManager;
	
	@Autowired
	private ThingIFInAppService thingIFService;
	
	@Autowired
	private ThingTagManager thingTagService;
	
	@Autowired
	private TriggerRecordDao triggerDao;

	@PostConstruct
	void init(){
		resendGatewayCommand();
	}

	@Scheduled(cron = "20 0/2 * * * ?")
	public void resendGatewayCommand(){
		Calendar currentDateCalendar = Calendar.getInstance();
//		currentDateCalendar.add(Calendar.HOUR_OF_DAY, -8);
		currentDateCalendar.add(Calendar.SECOND, -gatewayBeforeSecond);
		Date currentDate = currentDateCalendar.getTime();
		Calendar lastDayDateCalendar = Calendar.getInstance();
		lastDayDateCalendar.add(Calendar.DAY_OF_MONTH, 1);
		Date lastDayDate = lastDayDateCalendar.getTime();

		//query command
		QueryParam queryParam = new QueryParam();
		BucketClause bucketClause = new BucketClause();
		com.kii.extension.sdk.query.condition.AndLogic andLogic = new com.kii.extension.sdk.query.condition.AndLogic();
		bucketClause.setClause(andLogic);
		andLogic.addClause(new com.kii.extension.sdk.query.condition.Equal("title", "trigger"));
		andLogic.addClause(new com.kii.extension.sdk.query.condition.Equal("commandState", "SENDING"));
		andLogic.addClause(com.kii.extension.sdk.query.condition.Range.less("_modified", currentDate));
//		andLogic.addClause(new com.kii.extension.sdk.query.condition.Range("_modified",lastDayDate,false,currentDate,false));
		bucketClause.setOrderBy("_modified");
		bucketClause.setDescending(true);
		queryParam.setBucketQuery(bucketClause);

		List<ThingCommand> gatewayCommands = thingIFService.queryCommandFull(gatewayFullThingID, queryParam);
		log.info("resendGatewayCommand commands:" + gatewayCommands.size());
		gatewayCommands.forEach( command -> {
			int resendCount = command.getMetadata().get("resendCount")==null ? 1 : Integer.valueOf(command.getMetadata().get("resendCount").toString());
//			if(resendCount > 3) {
//				log.error("resendGatewayCommand-error resend more than 3 still fail! resendCount: "+ resendCount + " commandId: " + command.getId());
//			}
			command.getMetadata().put("resendCount", resendCount+1);
			String newCommandId = thingIFService.sendCommand(command, gatewayFullThingID);
			log.error("resendGatewayCommand resend command resendCount: "+ resendCount + " newCommandId: " + newCommandId );
			thingIFService.deleteCommand(gatewayFullThingID, command.getId());
		});
	}

	
	public void disableTrigger(GatewayTriggerRecord record){
		
		sendGatewayCommand( record, GatewayTriggerOperate.GatewayCommand.disableTrigger);
		
	}
	
	public void deleteTrigger(GatewayTriggerRecord record){
		
		sendGatewayCommand( record, GatewayCommand.deleteTrigger);
		
	}
	
	public void enableTrigger(GatewayTriggerRecord record){
		
		sendGatewayCommand( record, GatewayCommand.enableTrigger);
		
	}
	
	
	public TriggerRecord addGatewayTrigger(TriggerRecord record){
		
		if(checkLocalRule(record)){

			try {
				return createGatewayRecord((SummaryTriggerRecord) record);
			} catch (Exception e) {
				log.error("createGatewayRecord error" , e);
				return record;
			}

		}else{
			return record;
		}
	}
	
	
	private   GatewayTriggerRecord createGatewayRecord(SummaryTriggerRecord  summaryRecord){
		
		
		GatewayTriggerRecord gatewayTriggerRecord = convertToGatewayTriggerRecord(summaryRecord);
		
		triggerDao.addKiiEntity(gatewayTriggerRecord);
		
		sendGatewayCommand(gatewayTriggerRecord,GatewayCommand.createTrigger);
		
		return gatewayTriggerRecord;
		
	}
	
	public TriggerRecord updateGatewayTrigger(TriggerRecord newRecord,boolean isGateway,boolean canGateway){
		
		
		if(isGateway) {
			GatewayTriggerRecord gwTrigger=(GatewayTriggerRecord)newRecord;
			
			if(canGateway) {
				triggerDao.updateEntity(newRecord, newRecord.getId());
				sendGatewayCommand(gwTrigger, GatewayCommand.updateTrigger);
				return newRecord;
			}else{
				SummaryTriggerRecord inSummaryTriggerRecord=gwTrigger.getSummaryTriggerInstance();
				
				inSummaryTriggerRecord.setRecordStatus(TriggerRecord.StatusType.enable);
				inSummaryTriggerRecord.fillCreator(inSummaryTriggerRecord.getUserID());
				triggerDao.updateEntityAll(inSummaryTriggerRecord, inSummaryTriggerRecord.getId());
				//delete from gateway
				sendGatewayCommand(gwTrigger, GatewayCommand.deleteTrigger);
				return inSummaryTriggerRecord;
			}
		}else {
		
			if(canGateway) {
				GatewayTriggerRecord gatewayTriggerRecord = null;
				try {
					gatewayTriggerRecord = convertToGatewayTriggerRecord((SummaryTriggerRecord) newRecord);
				} catch (Exception e) {
					log.error("updateGatewayRecord error" , e);
					return newRecord;
				}
				triggerDao.updateEntityAll(gatewayTriggerRecord, gatewayTriggerRecord.getId());
				sendGatewayCommand(gatewayTriggerRecord, GatewayCommand.updateTrigger);
				return gatewayTriggerRecord;
			}else{
				return newRecord;
			}

		}
	}


	
	private enum GatewayCommand{
		
		deleteTrigger,disableTrigger,enableTrigger,createTrigger,updateTrigger;
	}


	public  boolean checkLocalRuleCondition(Condition condition){
		if( ! ( condition instanceof Equal
				|| condition instanceof NotLogic
				|| condition instanceof AndLogic
				|| condition instanceof OrLogic) ){
			return false;
		}
		return true;
	}
	public  boolean checkLocalRule(TriggerRecord record) {

		
		if(record.getTargets().stream().anyMatch( t-> !( t.getType().equals(ExecuteTarget.TargetType.ThingCommand) || t.getType().equals(ExecuteTarget.TargetType.ThingCommandInGW) ) )){
			return false;
		}
		
		if( ! (  ( ( record instanceof SummaryTriggerRecord)||(record instanceof GatewayTriggerRecord) )
				&& record.getPredicate().getTriggersWhen().equals(WhenType.CONDITION_TRUE)
				&& record.getPreparedCondition() == null
				&& record.getPredicate().getSchedule() == null

//				&& ( (AndLogic) record.getPredicate().getCondition() ).getClauses().size() <= 2
//				&& ( (AndLogic) record.getPredicate().getCondition() ).getClauses().get(0) instanceof AndLogic
//				&& ( (AndLogic) ( ( (AndLogic) record.getPredicate().getCondition() ).getClauses().get(0) ) ).getClauses().size() == 2
//			    && ( (AndLogic) ( ( (AndLogic) record.getPredicate().getCondition() ).getClauses().get(0) ) ).getClauses().get(0) instanceof SimpleCondition
		) ) {
			
			return false;
		}

		if(checkLocalRuleCondition(record.getPredicate().getCondition()) == false) {
			return false;
		}
		if(record instanceof  SummaryTriggerRecord) {
			SummaryTriggerRecord summaryTriggerRecord = (SummaryTriggerRecord) record;
			
			//source only one thing
			Collection<SummarySource> sourceCollection = summaryTriggerRecord.getSummarySource().values();
			Iterator<SummarySource> sourceIterator = sourceCollection.iterator();
			while (sourceIterator.hasNext()) { //每个SummarySource只有 单个 thing
				SummarySource summarySource = sourceIterator.next();
				TagSelector selector = summarySource.getSource().getSelector();
				if ( (selector.getTagList() != null && selector.getTagList().size() > 0)
						|| (summarySource.getSource().getThingList() != null && summarySource.getSource().getThingList().size() != 1)) {
					return false;
				}
			}
		}else{
			GatewayTriggerRecord  summaryTriggerRecord = (GatewayTriggerRecord) record;
			
			//source only one thing
			Collection<GatewaySummarySource> sourceCollection = summaryTriggerRecord.getSummarySource().values();
			Iterator<GatewaySummarySource> sourceIterator = sourceCollection.iterator();
			while (sourceIterator.hasNext()) { //每个SummarySource只有 单个 thing
				SummarySource summarySource = sourceIterator.next();
				TagSelector selector = summarySource.getSource().getSelector();
				if ((selector.getTagList() != null && selector.getTagList().size() > 0)
						|| (summarySource.getSource().getThingList() != null && summarySource.getSource().getThingList().size() != 1)) {
					return false;
				}
			}
		}
		
		return true ;
	}
	
	private GatewayTriggerRecord convertToGatewayTriggerRecord(SummaryTriggerRecord summaryRecord) {
		GatewayTriggerRecord gatewayTriggerRecord = new GatewayTriggerRecord();
		BeanUtils.copyProperties(summaryRecord, gatewayTriggerRecord, "summarySource", "targets");
		gatewayTriggerRecord.setRecordStatus(TriggerRecord.StatusType.enable);
		
		Collection<SummarySource> sourceCollection = summaryRecord.getSummarySource().values();
		Iterator<SummarySource> sourceIterator = sourceCollection.iterator();
		
		
		GlobalThingInfo sourceThing = thingTagService.findByID(sourceIterator.next().getSource().getThingList().get(0));
		//
		ThingOfKiiCloud gatewayOfKiiCloud = null;
		try {
			gatewayOfKiiCloud = thingIFService.getThingGateway(sourceThing.getFullKiiThingID());
			log.info("gwtrigger thing of gateway:" + gatewayOfKiiCloud.getThingID());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
		String thingID=gatewayOfKiiCloud.getThingID();
		String fullKiiThingID= ThingIDTools.joinFullKiiThingID(sourceThing.getKiiAppID(), thingID);
		//query all things of the gateway
		List<EndNodeOfGateway> allEndNodesOfGateway = thingIFService.getAllEndNodesOfGateway(fullKiiThingID);
		Map<String, EndNodeOfGateway> allEndNodesOfGatewayMap = new HashMap<>();
		allEndNodesOfGateway.forEach(endNodeOfGateway -> allEndNodesOfGatewayMap.put(endNodeOfGateway.getVendorThingID(), endNodeOfGateway));
		
		//source in same gateway
		summaryRecord.getSummarySource().forEach( (key , summarySource )-> {
			//每个SummarySource只有 单个 thing
			GlobalThingInfo sourceThingTemp = thingTagService.findByID(summarySource.getSource().getThingList().get(0));
			if (allEndNodesOfGatewayMap.get(sourceThingTemp.getVendorThingID()) == null) {
				throw new IllegalStateException();
			}
			GatewaySummarySource gatewaySummarySource = new GatewaySummarySource();
			BeanUtils.copyProperties(summarySource , gatewaySummarySource);
			gatewaySummarySource.getSourceVendorThing().getVendorThingIdList().add(sourceThingTemp.getVendorThingID());
			gatewaySummarySource.getSourceVendorThing().getThingList().add(sourceThingTemp.getId());
			gatewayTriggerRecord.getSummarySource().put(key, gatewaySummarySource);
		});
		
		//command task
		List<ExecuteTarget> targets = summaryRecord.getTargets();
		for(ExecuteTarget target:targets)
			switch (target.getType()) {
				
				case ThingCommand:
					CommandToThing command = (CommandToThing) target;
					CommandToThingInGW cmdInGW = new CommandToThingInGW();
					cmdInGW.setCommand(command.getCommand());

//					Set<GlobalThingInfo> thingList = thingTagService.getThingInfos(command.getSelector());
					
					for (String thingId : command.getThingList()) {
						GlobalThingInfo thing = thingTagService.getThingByID(Long.valueOf(thingId));
						if (allEndNodesOfGatewayMap.get(thing.getVendorThingID()) == null) {
							throw new IllegalStateException();
						}
						cmdInGW.getSelector().getVendorThingIdList().add(thing.getVendorThingID());
						cmdInGW.getSelector().getThingList().add(thing.getId());
					}
					gatewayTriggerRecord.addTarget(cmdInGW);
					break;
				case HttpApiCall:
					throw new IllegalStateException();
				
			}
		
		String vendorThingID=thingTagService.getThingByFullKiiThingID(sourceThing.getKiiAppID(), thingID).getVendorThingID();
		gatewayTriggerRecord.setGatewayVendorThingID(vendorThingID);
		gatewayTriggerRecord.setGatewayFullKiiThingID(fullKiiThingID);
		return gatewayTriggerRecord;
	}
	
	
	
	private  void sendGatewayCommand(GatewayTriggerRecord record, GatewayCommand act ) {
		
		
		String triggerID=record.getTriggerID();
		
		String fullThingID=record.getGatewayFullKiiThingID();
		
		Map<String, Action> actions = new HashMap<>();
		Action action = new Action();
		actions.put(act.name(), action);
		
		if(act == GatewayCommand.createTrigger || act == GatewayCommand.updateTrigger){
			action.setField("triggerJson", record);
		}else {
			action.setField("triggerID", triggerID);
		}
		//command								send to gateway
		ThingCommand command = new ThingCommand();
		command.setSchema("gateway");
		command.setTitle("trigger");
		//action
		command.setActions(Arrays.asList(actions));
		
		ThingIDTools.ThingIDCombine combine = ThingIDTools.splitFullKiiThingID(fullThingID);
		
		command.setUserID(appInfoManager.getDefaultOwer(combine.kiiAppID).getUserID());
		thingIFService.sendCommand(command, fullThingID);
		
	}
	
}
