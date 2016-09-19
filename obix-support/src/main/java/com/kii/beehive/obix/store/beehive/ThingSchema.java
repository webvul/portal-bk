package com.kii.beehive.obix.store.beehive;

import java.util.HashMap;
import java.util.Map;

public class ThingSchema {


	private String name;

	private StatusContains statesSchema;

	private Map<String,ActionContains>  actions=new HashMap<>();


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StatusContains getStatesSchema() {
		return statesSchema;
	}

	public void setStatesSchema(StatusContains statesSchema) {
		this.statesSchema = statesSchema;
	}

	public Map<String, ActionContains> getActions() {
		return actions;
	}

	public void setActions(Map<String, ActionContains> actions) {
		this.actions = actions;
	}
}
