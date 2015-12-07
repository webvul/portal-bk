package com.kii.extension.sdk.entity.thingif;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDetail extends ThingCommand {

	private CommandState state;

	private Map<String,ActionResult> actionResults=new HashMap<>();

	private String firedByTriggerID;

	private Date created;

	private Date modified;

	public CommandState getState() {
		return state;
	}

	public void setState(CommandState state) {
		this.state = state;
	}

	public Map<String, ActionResult> getActionResults() {
		return actionResults;
	}

	public void setActionResults(Map<String, ActionResult> actionResults) {
		this.actionResults = actionResults;
	}

	public String getFiredByTriggerID() {
		return firedByTriggerID;
	}

	public void setFiredByTriggerID(String firedByTriggerID) {
		this.firedByTriggerID = firedByTriggerID;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
}
