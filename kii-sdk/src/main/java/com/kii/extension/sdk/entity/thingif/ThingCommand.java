package com.kii.extension.sdk.entity.thingif;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ThingCommand {

	/*

	{"issuer":"USER:92803ea00022-a488-4e11-d7c1-018317e4","actions":[{"power":true}],"schema":"threaddemo","schemaVersion":0,"metadata":{"foo":"bar"}}
	 */

	private String commandID;

	private String issuer;

	private String target;

	private List<Map<String,Action>> actions=new ArrayList<>();

	private  List<Map<String,ActionResult>> actionResults=new ArrayList<>();

	private String schema;

	private int schemaVersion;

	private String title;

	private String description;

	private Map<String,Object> metadata=new HashMap<>();

	private CommandStateType  commandState;


	public String getCommandID() {
		return commandID;
	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public CommandStateType getCommandState() {
		return commandState;
	}

	public void setCommandState(CommandStateType commandState) {
		this.commandState = commandState;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	@JsonIgnore
	public void setUserID(String userID){
		setIssuer("USER:"+userID);
	}

	public List<Map<String,Action>>  getActions() {
		return actions;
	}

	@JsonIgnore
	public void addAction(String name,Action action){
		Map<String,Action> map= Collections.singletonMap(name,action);
		this.actions.add(map);
	}

	public List<Map<String, ActionResult>> getActionResults() {
		return actionResults;
	}

	public void setActionResults(List<Map<String, ActionResult>> actionResults) {
		this.actionResults = actionResults;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public int getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(int schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	@JsonIgnore
	public void addMetadata(String key,Object value){
		this.metadata.put(key,value);
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

	public void setActions(List<Map<String, Action>> actions) {
		this.actions = actions;
	}
}
