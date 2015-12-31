package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingCommand;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = SimpleTriggerRecord.class,name="Simple"),
		@JsonSubTypes.Type(value = GroupTriggerRecord.class,name="Group"),
		@JsonSubTypes.Type(value = SummaryTriggerRecord.class,name="Summary"),
})
public abstract  class TriggerRecord extends KiiEntity {

	private StatePredicate  perdicate;

	private List<TriggerTarget>  targets=new ArrayList<>();

//	private TriggerType type;



	public StatePredicate getPerdicate() {
		return perdicate;
	}

	public void setPerdicate(StatePredicate perdicate) {
		this.perdicate = perdicate;
	}

	public List<TriggerTarget> getTargets() {
		return targets;
	}

	public void setTarget(List<TriggerTarget> target) {
		this.targets = target;
	}

	@JsonIgnore
	public void addTarget(TriggerTarget  target){
		this.targets.add(target);
	}

//	public void setType(TriggerType type) {
//		this.type = type;
//	}

	public static enum TriggerType{
		Simple,Group,Summary;
	}

}
