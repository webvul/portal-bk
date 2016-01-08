package com.kii.beehive.portal.store.entity.trigger;


import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.extension.sdk.entity.KiiEntity;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = SimpleTriggerRuntimeState.class,name="Simple"),
		@JsonSubTypes.Type(value = GroupTriggerRuntimeState.class,name="Group"),
		@JsonSubTypes.Type(value = SummaryTriggerRuntimeState.class,name="Summary"),
})
public class TriggerRuntimeState extends KiiEntity {


	private Set<String> thingIDSet=new HashSet<>();

	public Set<String> getThingIDSet() {
		return thingIDSet;
	}

	public void setThingIDSet(Set<String> thingIDSet) {
		this.thingIDSet = thingIDSet;
	}

	@JsonIgnore
	public void addThingID(String thingID){
		thingIDSet.add(thingID);
	}

	private BeehiveTriggerType type;

	public BeehiveTriggerType getType() {
		return type;
	}

	public void setType(BeehiveTriggerType type) {
		this.type = type;
	}
}
