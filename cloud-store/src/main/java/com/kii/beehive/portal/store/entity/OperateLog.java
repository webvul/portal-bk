package com.kii.beehive.portal.store.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.kii.extension.sdk.entity.KiiEntity;

public class   OperateLog extends KiiEntity{

	private String  source;

	private OperateType  sourceType;

	private ActionType action;

	private String actionType;

	private Map<String,Object> object=new HashMap<>();

	private Date timestamp=new Date();

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public OperateType getSourceType() {
		return sourceType;
	}

	public void setSourceType(OperateType sourceType) {
		this.sourceType = sourceType;
	}

	public ActionType getAction() {
		return action;
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public Map<String,Object> getObject() {
		return object;
	}

	public void setObject(Map<String,Object> object) {
		this.object = object;
	}

	public void addField(String field,Object val){
		this.object.put(field,val);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public enum OperateType{

		trigger;
	}


	public enum ActionType{
		create,delete,enable,disable,fire,update;
	}


}
