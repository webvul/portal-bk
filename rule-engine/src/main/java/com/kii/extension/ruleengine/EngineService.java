package com.kii.extension.ruleengine;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.extension.ruleengine.drools.DroolsTriggerService;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.drools.entity.Group;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.Thing;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicy;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.SummaryFunSource;
import com.kii.extension.ruleengine.store.trigger.multiple.ThingSource;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class EngineService {


	@Autowired
	private DroolsTriggerService  droolsTriggerService;

	@Autowired
	private RuleGeneral  ruleGeneral;


//	private Set<String>  scheduleSet=new ConcurrentSkipListSet<>();


	//TODO:need been finish
	public void createMultipleSourceTrigger(MultipleSrcTriggerRecord record,Map<String,Set<String> > thingMap){


		Trigger trigger=new Trigger();
		trigger.setType(TriggerType.multiple);
		trigger.setTriggerID(record.getId());
		trigger.setStream(false);

		String drl=ruleGeneral.generMultipleDrlConfig(record);

		droolsTriggerService.addMultipleTrigger(trigger,drl);

		record.getSummarySource().forEach((name,src)->{

			    switch(src.getType()){
					case summary:
						SummaryFunSource source=(SummaryFunSource)src;

						Summary summary=new Summary();
						summary.setTriggerID(trigger.getTriggerID());
						summary.setFieldName(source.getStateName());
						summary.setFunName(source.getFunction().name());
						summary.setName(name);
						summary.setThingCol(thingMap.get(name));

						droolsTriggerService.addTriggerData(summary);
						break;
					case group:

						Group group=new Group();
						group.setName(name);
						group.setThingCol(thingMap.get(name));
						group.setTriggerID(trigger.getTriggerID());

						droolsTriggerService.addTriggerData(group);
						break;
					case thing:

						ThingSource  thingSrc=(ThingSource)src;
						Thing thing=new Thing();
						thing.setTriggerID(trigger.getTriggerID());
						thing.setName(name);
						thing.setFieldName(thingSrc.getStateName());
						thing.setThingID(thingSrc.getThingID());

						droolsTriggerService.addTriggerData(thing);
						break;
					default:
					}


				}
		);

	}


	/**
	 * 清空 drools 相关 重新初始化
	 */
	public void clear(){
		droolsTriggerService.clear();
	}


	public Map<String,Object>  dumpEngineRuntime(){

		return droolsTriggerService.getEngineRuntime();

	}

	public void createSummaryTrigger(SummaryTriggerRecord record, Map<String,Set<String> > summaryMap,boolean isStream){


		Trigger trigger=new Trigger();

		trigger.setTriggerID(record.getId());
		trigger.setType(TriggerType.summary);
		trigger.setWhen(record.getPredicate().getTriggersWhen());
		trigger.setStream(isStream);
		trigger.setEnable(record.getRecordStatus()== TriggerRecord.StatusType.enable);

		String 	rule = ruleGeneral.generDrlConfig(record.getId(), TriggerType.summary, record.getPredicate());

		droolsTriggerService.addSummaryTrigger(trigger,rule);

		record.getSummarySource().forEach((k,v)->{

			v.getExpressList().forEach((exp)->{

				Summary summary=new Summary();
				summary.setTriggerID(trigger.getTriggerID());
				summary.setFieldName(exp.getStateName());

				summary.setName(k+"."+exp.getSummaryAlias());
				summary.setThingCol(summaryMap.get(k));

				if(exp.getSlideFuntion()!=null){
					String drl=ruleGeneral.generSlideConfig(trigger.getTriggerID(),k,exp);
					summary.setFunName(exp.getFunction().name());
					droolsTriggerService.addSlideSummary(summary,drl);

				}else{
					summary.setFunName(exp.getFunction().name());
					droolsTriggerService.addTriggerData(summary);
				}

			});

		});

		droolsTriggerService.fireCondition();

	}

	public void createGroupTrigger(Collection<String> thingIDs, GroupTriggerRecord record){


		Trigger trigger=new Trigger();

		trigger.setTriggerID(record.getId());
		trigger.setType(TriggerType.group);

		Group group=new Group();

		TriggerGroupPolicy policy=record.getPolicy();
		group.setPolicy(policy.getGroupPolicy());
		group.setNumber(policy.getCriticalNumber());
		group.setTriggerID(record.getId());
		group.setName("comm");

		trigger.setStream(false);

		RuleEnginePredicate predicate=record.getPredicate();

		trigger.setWhen(predicate.getTriggersWhen());

		group.setThingCol(thingIDs);

		trigger.setEnable(record.getRecordStatus()== TriggerRecord.StatusType.enable);

		String rule=ruleGeneral.generGroupDrlConfig(record.getId(),policy.getGroupPolicy(),predicate);

		droolsTriggerService.addTrigger(trigger,rule);
		droolsTriggerService.addTriggerData(group);

		droolsTriggerService.fireCondition();

	}


	public void  createSimpleTrigger(String thingID, SimpleTriggerRecord record)  {


		Trigger trigger=new Trigger();

		String triggerID=record.getId();

		trigger.setTriggerID(triggerID);
		trigger.setType(TriggerType.simple);
		trigger.setStream(false);
		trigger.setWhen(record.getPredicate().getTriggersWhen());

		trigger.setEnable(TriggerRecord.StatusType.enable == record.getRecordStatus());

		String rule=ruleGeneral.generDrlConfig(triggerID,TriggerType.simple,record.getPredicate());

		droolsTriggerService.addTrigger(trigger,rule);

		if(!StringUtils.isEmpty(thingID)) {
			Thing thing=new Thing();
			thing.setThingID(thingID);
			thing.setTriggerID(triggerID);
			thing.setName("comm");
			droolsTriggerService.addTriggerData(thing);
		}

		droolsTriggerService.fireCondition();

	}


	public void changeThingsInTrigger(String triggerID,Set<String> newThings){

		droolsTriggerService.updateThingsWithName(triggerID,"comm",newThings);

	}


	public void changeThingsInSummary(String triggerID,String summaryName,Set<String> newThings){

		droolsTriggerService.updateThingsWithName(triggerID,summaryName,newThings);

	}

	public void initThingStatus(List<ThingInfo> thingInfos) {

		droolsTriggerService.setInitSign(true);

		thingInfos.forEach(th->droolsTriggerService.initThingStatus(th.getThingStatusInRule()));

		droolsTriggerService.fireCondition();
		droolsTriggerService.setInitSign(false);
	}

	public void updateThingStatus(String thingID,ThingStatus status,Date time) {


		ThingStatusInRule newStatus=new ThingStatusInRule();
		newStatus.setThingID(thingID);
		newStatus.setValues(status.getFields());
		newStatus.setCreateAt(time);

		droolsTriggerService.addThingStatus(newStatus);
	}


	public static   class ThingInfo{

		private String thingID;

		private ThingStatus status;

		private Date date;

		public ThingStatusInRule getThingStatusInRule(){
			ThingStatusInRule newStatus=new ThingStatusInRule();

			newStatus.setThingID(thingID);
			newStatus.setValues(status.getFields());
			newStatus.setCreateAt(date);

			return newStatus;
		}

		public void setThingID(String thingID) {
			this.thingID = thingID;
		}


		public void setStatus(ThingStatus status) {
			this.status = status;
		}

		public void setDate(Date date) {
			this.date = date;
		}
	}
	
	public void disableTrigger(String triggerID) {

		droolsTriggerService.disableTrigger(triggerID);

	}




	public void enableTrigger(String triggerID) {


		droolsTriggerService.enableTrigger(triggerID);

		droolsTriggerService.fireCondition();

	}

	public void removeTrigger(String triggerID){

		droolsTriggerService.removeTrigger(triggerID);

	}
}
