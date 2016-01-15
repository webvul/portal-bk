package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.thingif.StatePredicate;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = SimpleTriggerRecord.class,name="Simple"),
		@JsonSubTypes.Type(value = GroupTriggerRecord.class,name="Group"),
		@JsonSubTypes.Type(value = SummaryTriggerRecord.class,name="Summary"),
})
public abstract  class TriggerRecord extends KiiEntity {

	private String userID;

	private StatePredicate  perdicate;

	private List<TriggerTarget>  targets=new ArrayList<>();

//	private TriggerType type;

	private StatusType recordStatus;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public StatusType getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(StatusType recordStatus) {
		this.recordStatus = recordStatus;
	}

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

	private BeehiveTriggerType type;

	public void setType(BeehiveTriggerType type) {
		this.type = type;
	}

	public abstract  BeehiveTriggerType getType();

	public enum StatusType{
		enable,disable,deleted;
	}
}
