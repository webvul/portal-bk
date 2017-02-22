package com.kii.beehive.business.ruleengine.entitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.beehive.portal.store.entity.trigger.CommandParam;
import com.kii.beehive.portal.store.entity.trigger.RuleEnginePredicate;
import com.kii.beehive.portal.store.entity.trigger.schedule.TriggerValidPeriod;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = EngineSimpleTrigger.class, name = "Simple"),
		@JsonSubTypes.Type(value = EngineMultipleSrcTrigger.class, name = "Multiple")
})
public abstract class EngineTrigger {

	private Map<String,Object> customProperty=new HashMap<>();
	
	private String creator;

	private TriggerValidPeriod period;

	private RuleEnginePredicate predicate;

	private List<EngineExecuteTarget> targets = new ArrayList<>();

	private List<CommandParam> targetParamList = new ArrayList<>();

	private StatusType recordStatus= StatusType.enable;

	private String deletedReason;

	private String name;

	private String description;
	
	private String triggerID;
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getDeletedReason() {
		return deletedReason;
	}

	public void setDeletedReason(String deletedReason) {
		this.deletedReason = deletedReason;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID){
		this.triggerID=triggerID;
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

	public RuleEnginePredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(RuleEnginePredicate predicate) {
		this.predicate = predicate;
	}

	public List<EngineExecuteTarget> getTargets() {
		return targets;
	}

	public void setTargets(List<EngineExecuteTarget> target) {
		this.targets = target;
	}

	@JsonIgnore
	public void addTarget(EngineExecuteTarget target) {
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


	public Map<String, Object> getCustomProperty() {
		return customProperty;
	}

	public void setCustomProperty(Map<String, Object> customProperty) {
		this.customProperty = customProperty;
	}
}
