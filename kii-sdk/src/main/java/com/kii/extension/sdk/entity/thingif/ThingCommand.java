package com.kii.extension.sdk.entity.thingif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingCommand {

	/*

	{"issuer":"USER:92803ea00022-a488-4e11-d7c1-018317e4","actions":[{"power":true}],"schema":"demo","schemaVersion":0,"metadata":{"foo":"bar"}}
	 */

	private String issuer;

	private Map<String,Action> actions=new HashMap<>();

	private String schema;

	private int schemaVersion;

	private String title;

	private String description;

	private Map<String,Object> metadata=new HashMap<>();

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public Map<String,Action>  getActions() {
		return actions;
	}

	public void setActions(Map<String,Action>  actions) {
		this.actions = actions;
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
}
