package com.kii.extension.sdk.entity.trigger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ThingTrigger {

	private String triggerID;

	private TriggerTarget target;

	private Predicate predicate;

	private TargetCommand command;

	private ServiceCode serviceCode;

	private String title;

	private String description;

	private Map<String,Object> metadata=new HashMap<>();


	@JsonProperty("triggersWhat")
	public TriggerTarget getTarget() {
		return target;
	}

	public void setTarget(TriggerTarget target) {
		this.target = target;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	public TargetCommand getCommand() {
		return command;
	}

	public void setCommand(TargetCommand command) {
		this.command = command;
	}

	@JsonProperty("serverCode")
	public ServiceCode getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(ServiceCode serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	@JsonIgnore
	public void addMetadata(String key, Object value) {
		this.metadata.put(key,value);
	}

	@JsonIgnore
	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

}
