package com.kii.beehive.obix.store.beehive;

import java.util.HashMap;
import java.util.Map;

public class ThingSchema {

	private Map<String,PointDetail> statesSchema=new HashMap<>();

	private Map<String,Action>  actions=new HashMap<>();


	public Map<String, PointDetail> getStatesSchema() {
		return statesSchema;
	}

	public void setStatesSchema(Map<String, PointDetail> statesSchema) {
		this.statesSchema = statesSchema;
	}

	public Map<String, Action> getActions() {
		return actions;
	}

	public void setActions(Map<String, Action> actions) {
		this.actions = actions;
	}
}
