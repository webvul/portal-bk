package com.kii.extension.sdk.entity.thingif;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.Trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.extension.sdk.entity.thingif.conditions.LogicTriggerCondition;
import com.kii.extension.sdk.entity.thingif.conditions.TriggerCondition;

public class StatePredicate extends Predicate {

	private TriggerWhen triggersWhen;

	private TriggerCondition condition;

	@Override
	public EventSourceType getEventSource() {
		return EventSourceType.states;
	}

	public TriggerWhen getTriggersWhen() {
		return triggersWhen;
	}

	public void setTriggersWhen(TriggerWhen triggersWhen) {
		this.triggersWhen = triggersWhen;
	}

	@JsonIgnore
	public TriggerCondition getCondition() {
		return condition;
	}

	@JsonProperty("condition")
	public Map<String,Object>  getConditionForJson(){

		if(condition instanceof LogicTriggerCondition){
			return Collections.singletonMap(condition.getType().name(),((LogicTriggerCondition)condition).getClauses());
		}else{
			return Collections.singletonMap(condition.getType().name(),condition);
		}

	}

	public void setCondition(TriggerCondition condition) {
		this.condition = condition;
	}
}
