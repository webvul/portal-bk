package com.kii.beehive.obix.store.beehive;

import java.util.HashMap;
import java.util.Map;

public class ThingSchema {

	private StatusContains statesSchema;

	private Map<String,ActionContains>  actions=new HashMap<>();

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
