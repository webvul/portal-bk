package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.schedule.TriggerValidPeriod;
import com.kii.extension.sdk.entity.KiiEntity;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = SimpleTriggerRecord.class, name = "Simple"),
		@JsonSubTypes.Type(value = GroupTriggerRecord.class, name = "Group"),
		@JsonSubTypes.Type(value = SummaryTriggerRecord.class, name = "Summary"),
		@JsonSubTypes.Type(value = MultipleSrcTriggerRecord.class, name = "Multiple"),
		@JsonSubTypes.Type(value = GatewayTriggerRecord.class,name="Gateway" )
})
public abstract class TriggerRecord extends KiiEntity {

	private Map<String,Object> customProperty=new HashMap<>();

	private Long userID;

	private TriggerValidPeriod period;

	private RuleEnginePredicate predicate;

	private List<ExecuteTarget> targets = new ArrayList<>();

	private List<CommandParam> targetParamList = new ArrayList<>();

	private StatusType recordStatus;

	private String deletedReason;

	private String name;

	private String description;

	public String getDeletedReason() {
		return deletedReason;
	}

	public void setDeletedReason(String deletedReason) {
		this.deletedReason = deletedReason;
	}

	public String getTriggerID() {
		return super.getId();
	}

	public void setTriggerID(String triggerID){
		this.setId(triggerID);
	}

	@JsonProperty("prepareCondition")
	public TriggerValidPeriod getPreparedCondition() {
		return period;
	}

	public void setPreparedCondition(TriggerValidPeriod preparedCondition) {
		this.period = preparedCondition;
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

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
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

	public void setTargets(List<ExecuteTarget> target) {
		this.targets = target;
	}

	@JsonIgnore
	public void addTarget(ExecuteTarget target) {
		this.targets.add(target);
	}

	private BeehiveTriggerType type;

	public void setType(BeehiveTriggerType type) {
		this.type = type;
	}

	public abstract BeehiveTriggerType getType();

	public enum StatusType {
		enable, disable, deleted;
	}

	public void addTargetParam(String name, String param) {
		CommandParam cmdParam = new CommandParam();
		cmdParam.setExpress(param);
		cmdParam.setName(name);

		targetParamList.add(cmdParam);

	}

	public List<CommandParam> getTargetParamList() {
		return targetParamList;
	}

	public void setTargetParamList(List<CommandParam> targetParamList) {
		this.targetParamList = targetParamList;
	}


	@JsonGetter("createDate")
	public Date getCreateDate(){
		return super.getCreated();
	}


	public Map<String, Object> getCustomProperty() {
		return customProperty;
	}

	public void setCustomProperty(Map<String, Object> customProperty) {
		this.customProperty = customProperty;
	}
}
