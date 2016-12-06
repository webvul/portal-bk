package com.kii.beehive.business.ruleengine;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.drools.core.time.impl.CronExpression;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.exception.InvalidTriggerFormatException;
import com.kii.extension.ruleengine.store.trigger.GatewayTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.target.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.target.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.schedule.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.schedule.SchedulePrefix;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.condition.AndLogic;
import com.kii.extension.ruleengine.store.trigger.condition.OrLogic;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SourceElement;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

/**
 * Created by Arno on 16/3/31.
 */
@Component
public class TriggerValidate {

    public void validateTrigger(TriggerRecord triggerRecord){
        if(triggerRecord instanceof SimpleTriggerRecord){
            validateSimpleTrigger((SimpleTriggerRecord)triggerRecord);
        }else if(triggerRecord instanceof GroupTriggerRecord){
			GroupTriggerRecord  groupTrigger=(GroupTriggerRecord)triggerRecord;

			validateTagSelector(groupTrigger.getSource());

        }else if(triggerRecord instanceof SummaryTriggerRecord){
            SummaryTriggerRecord  summary=((SummaryTriggerRecord)triggerRecord);
			summary.getSummarySource().values().forEach((k)->{
				validateTagSelector(k.getSource());
			});
        }else if(triggerRecord instanceof MultipleSrcTriggerRecord) {
			MultipleSrcTriggerRecord mulTrigger=(MultipleSrcTriggerRecord)triggerRecord;
			for (SourceElement s : mulTrigger.getSummarySource().values()) {
				if(s instanceof GroupSummarySource){
					validateTagSelector(((GroupSummarySource)s).getSource());
				}
			}
		}else if(triggerRecord instanceof GatewayTriggerRecord) {
		}else{
            throw new InvalidTriggerFormatException("Unsupported trigger type !");
        }

        RuleEnginePredicate predicate = triggerRecord.getPredicate();

        if(predicate == null){
            throw new InvalidTriggerFormatException("Condition can not be null !");
        }

		validatePredicate(predicate);


		List<ExecuteTarget> executeTargets = triggerRecord.getTargets();

		boolean isDelay=executeTargets.stream().filter(e-> StringUtils.isNotBlank(e.getDelay())).count()>0;

		if(isDelay && triggerRecord.getPredicate().getTriggersWhen()== WhenType.CONDITION_TRUE ){
			throw new InvalidTriggerFormatException("in condition true mode,cannot use delay execute ");
		}

		if(executeTargets == null || executeTargets.size() == 0){
            throw new InvalidTriggerFormatException("ExecuteTargets can not be null !");
        }

        validateTargets(executeTargets);



    }

    private void validateSimpleTrigger(SimpleTriggerRecord simpleTriggerRecord){
        RuleEnginePredicate predicate = simpleTriggerRecord.getPredicate();
        SimpleTriggerRecord.ThingID thingID = simpleTriggerRecord.getSource();

        //when condition exist , thingID is not null. And at the same time schedule express can not be null;
        if(thingID == null && predicate.getCondition()!=null){
            throw new InvalidTriggerFormatException("Source and Condition can not be null at the same time !");
        }

        if(thingID == null && predicate==null){
            throw new InvalidTriggerFormatException("Source and Schedule can not be null at the same time !");
        }



    }

    private void validateTagSelector(TagSelector triggerSource ){

        if(
            triggerSource == null  ||
            (
                (triggerSource.getTagList() == null || triggerSource.getTagList().size() == 0)
                &&
                (triggerSource.getThingList() == null || triggerSource.getThingList().size() == 0)
            )
          ){

            throw new InvalidTriggerFormatException("Group trigger source can not be null but you can create simple trigger !");
        }

        if (
                triggerSource.getTagList() != null && triggerSource.getTagList().size()>0
                &&
                triggerSource.getThingList() != null && triggerSource.getThingList().size()>0
           ){
            throw new InvalidTriggerFormatException("TagList && ThingList source can not exist at same time !");
        }

    }


    private void validatePredicate(RuleEnginePredicate predicate){
        Condition condition = predicate.getCondition();
        WhenType whenType = predicate.getTriggersWhen();
        SchedulePrefix schedulePeriod = predicate.getSchedule();

        if(condition == null && schedulePeriod == null){
            throw new InvalidTriggerFormatException("Condition and Schedule can not be null at the same time !");
        }

        if(condition != null && whenType == null){
            throw new InvalidTriggerFormatException("When condition is specified, triggerWhen can not be null at the same time !");
        }

        if(condition != null && schedulePeriod != null && !whenType.name().equals(WhenType.CONDITION_TRUE.name())){
            throw new InvalidTriggerFormatException("When the trigger is condition & schedule , whenType can only be equal to CONDITION_TRUE !");
        }

        if(schedulePeriod instanceof CronPrefix){
            Boolean isValide = CronExpression.isValidExpression(((CronPrefix) schedulePeriod).getCron());
            if (!isValide){
                throw new InvalidTriggerFormatException("The cron express is error ! \n '"+((CronPrefix) schedulePeriod).getCron()+"'");
            }
        }

        if(condition instanceof AndLogic && ((AndLogic) condition).getClauses().size()==1){
            throw new InvalidTriggerFormatException("When condition type is 'and', must have multiple logic in the clauses !");
        }
        if(condition instanceof OrLogic && ((OrLogic) condition).getClauses().size()==1){
            throw new InvalidTriggerFormatException("When condition type is 'or', must have multiple logic in the clauses !");

        }


    }

    private void validateTargets(List<ExecuteTarget> executeTargets){
        for(ExecuteTarget executeTarget : executeTargets){


			if(  executeTarget instanceof  CommandToThing) {

				CommandToThing ctt = (CommandToThing) executeTarget;

				TagSelector tagSelector = ctt.getSelector();
				ThingCommand command = ctt.getCommand();

				if (tagSelector == null) {
					throw new InvalidTriggerFormatException("TagSelector can not be null !");
				}

				if (tagSelector.getThingList() == null && tagSelector.getTagList() == null) {
					throw new InvalidTriggerFormatException("Thing can not be null !");
				}

				if (tagSelector.getThingList() != null && tagSelector.getTagList() != null && tagSelector.getTagList().size() > 0 && tagSelector.getThingList().size() > 0) {
					throw new InvalidTriggerFormatException("ThingList and TagList can not be specified at the same time !");
				}

				if (command == null || command.getActions() == null || command.getActions().size() == 0) {
					throw new InvalidTriggerFormatException("Command can not be null !");
				}
			}else if(executeTarget instanceof CallHttpApi){


			}

        }

    }
}
