package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.extension.ruleengine.sdk.entity.KiiEntity;


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

	private PreparedCondition preparedCondition;

	private RuleEnginePredicate predicate;

	private List<ExecuteTarget>  targets=new ArrayList<>();

//	private TriggerType type;

	private StatusType recordStatus;

	private String  name;

	private String description;

	public PreparedCondition getPreparedCondition() {
		return preparedCondition;
	}

	public void setPreparedCondition(PreparedCondition preparedCondition) {
		this.preparedCondition = preparedCondition;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public RuleEnginePredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(RuleEnginePredicate predicate) {
		this.predicate = predicate;
	}

	public List<ExecuteTarget> getTargets() {
		return targets;
	}

	public void setTarget(List<ExecuteTarget> target) {
		this.targets = target;
	}

	@JsonIgnore
	public void addTarget(ExecuteTarget target){
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
