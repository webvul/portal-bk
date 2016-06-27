package com.kii.extension.ruleengine;

import java.util.List;

import org.drools.core.time.impl.CronExpression;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.store.trigger.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SchedulePrefix;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.condition.AndLogic;
import com.kii.extension.ruleengine.store.trigger.condition.OrLogic;
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
            validateGroupTrigger((GroupTriggerRecord)triggerRecord);
        }else if(triggerRecord instanceof SummaryTriggerRecord){
            validateSummaryTrigger((SummaryTriggerRecord)triggerRecord);
        }else{
            throw new IllegalArgumentException("Unsupported trigger type !");
        }


        RuleEnginePredicate predicate = triggerRecord.getPredicate();
        List<ExecuteTarget> executeTargets = triggerRecord.getTargets();

        if(predicate == null){
            throw new IllegalArgumentException("Condition can not be null !");
        }

        if(executeTargets == null || executeTargets.size() == 0){
            throw new IllegalArgumentException("ExecuteTargets can not be null !");
        }

        validatePredicate(predicate);

        validateTargets(executeTargets);
    }

    private void validateSimpleTrigger(SimpleTriggerRecord simpleTriggerRecord){
        RuleEnginePredicate predicate = simpleTriggerRecord.getPredicate();
        SimpleTriggerRecord.ThingID thingID = simpleTriggerRecord.getSource();

        //when condition exist , thingID is not null. And at the same time schedule express can not be null;
        if(thingID == null && predicate.getCondition()!=null){
            throw new IllegalArgumentException("Source and Condition can not be null at the same time !");
        }

        if(thingID == null && predicate==null){
            throw new IllegalArgumentException("Source and Schedule can not be null at the same time !");
        }

        //source内判定行业模版,暂未实现(等待行业模板)


    }

    private void validateGroupTrigger(GroupTriggerRecord groupTriggerRecord){
		TagSelector triggerSource = groupTriggerRecord.getSource();
        RuleEnginePredicate predicate = groupTriggerRecord.getPredicate();

        if(
            triggerSource == null  ||
            (
                (triggerSource.getTagList() == null || triggerSource.getTagList().size() == 0)
                &&
                (triggerSource.getThingList() == null || triggerSource.getThingList().size() == 0)
            )
          ){

            throw new IllegalArgumentException("Group trigger source can not be null but you can create simple trigger !");
        }

        if (
                triggerSource.getTagList() != null && triggerSource.getTagList().size()>0
                &&
                triggerSource.getThingList() != null && triggerSource.getThingList().size()>0
           ){
            throw new IllegalArgumentException("TagList && ThingList source can not exist at same time !");
        }

        //source内判定行业模版,暂未实现(等待行业模板)
    }

    private void validateSummaryTrigger(SummaryTriggerRecord summaryTriggerRecord){

    }

    private void validatePredicate(RuleEnginePredicate predicate){
        Condition condition = predicate.getCondition();
        WhenType whenType = predicate.getTriggersWhen();
        SchedulePrefix schedulePeriod = predicate.getSchedule();

        if(condition == null && schedulePeriod == null){
            throw new IllegalArgumentException("Condition and Schedule can not be null at the same time !");
        }

        if(condition != null && whenType == null){
            throw new IllegalArgumentException("When condition is specified, triggerWhen can not be null at the same time !");
        }

        if(condition != null && schedulePeriod != null && !whenType.name().equals(WhenType.CONDITION_TRUE.name())){
            throw new IllegalArgumentException("When the trigger is condition & schedule , whenType can only be equal to CONDITION_TRUE !");
        }

        if(schedulePeriod instanceof CronPrefix){
            Boolean isValide = CronExpression.isValidExpression(((CronPrefix) schedulePeriod).getCron());
            if (!isValide){
                throw new IllegalArgumentException("The cron express is error ! \n '"+((CronPrefix) schedulePeriod).getCron()+"'");
            }
        }

        //如果是AndLogic与OrLogic,那么clauses中只有一组条件时是没意思的,NotLogic时可以是一组条件
        if(condition instanceof AndLogic && ((AndLogic) condition).getClauses().size()==1){
            throw new IllegalArgumentException("When condition type is 'and', must have multiple logic in the clauses !");
        }
        if(condition instanceof OrLogic && ((OrLogic) condition).getClauses().size()==1){
            throw new IllegalArgumentException("When condition type is 'or', must have multiple logic in the clauses !");

        }

        //condition内的数据不完整,还未判断

        //如果是schedule trigger,需要限制触发频率
    }

    private void validateTargets(List<ExecuteTarget> executeTargets){
        for(ExecuteTarget executeTarget : executeTargets){


			if(  executeTarget instanceof  CommandToThing) {

				CommandToThing ctt = (CommandToThing) executeTarget;

				TagSelector tagSelector = ctt.getSelector();
				ThingCommand command = ctt.getCommand();

				if (tagSelector == null) {
					throw new IllegalArgumentException("TagSelector can not be null !");
				}

				if (tagSelector.getThingList() == null && tagSelector.getTagList() == null) {
					throw new IllegalArgumentException("Thing can not be null !");
				}

				if (tagSelector.getThingList() != null && tagSelector.getTagList() != null && tagSelector.getTagList().size() > 0 && tagSelector.getThingList().size() > 0) {
					throw new IllegalArgumentException("ThingList and TagList can not be specified at the same time !");
				}

				if (command == null || command.getActions() == null || command.getActions().size() == 0) {
					throw new IllegalArgumentException("Command can not be null !");
				}
			}else if(executeTarget instanceof CallHttpApi){


			}

        }

    }
}
