package com.kii.extension.ruleengine.sdk.entity.thingif;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommandDetail extends ThingCommand {

	private CommandStateType state;

	private List<Map<String,ActionResult>> actionResults=new ArrayList<>();

	private String firedByTriggerID;

	private Date created;

	private Date modified;

	public CommandStateType getState() {
		return state;
	}

	public void setState(CommandStateType state) {
		this.state = state;
	}

	public List<Map<String,ActionResult>> getActionResults() {
		return actionResults;
	}

	public void setActionResults(List<Map<String,ActionResult>> actionResults) {
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
