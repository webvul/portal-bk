//package com.kii.extension.sdk.entity.thingif;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//public class CommandDetail  {
//
////	{
////		"schema" : "some schema",
////			"schemaVersion" : 1,
////			"task" : "thing:th.aba700e36100-6698-6e11-ff53-011a1535",
////			"commandState" : "SENDING",
////			"issuer" : "user:aba700e36100-4558-5e11-ad5b-07aea12c",
////			"actions" : [ {
////					"turnPower" : {
////						"power" : "on"
////					}
////				}, {
////					"changeColor" : {
////						"color" : "#123456"
////					}
////				} ],
////		"metadata" : { },
////		"commandID" : "5457ed80-35ff-11e6-8966-00163e007aba",
////			"createdAt" : 1466328142936,
////			"modifiedAt" : 1466328142936
////	}
//
//	private String commandID;
//
//	private CommandStateType commandState;
//
//	private List<Map<String,ActionResult>> actionResults=new ArrayList<>();
//
//	private String firedByTriggerID;
//
//	private Date createdAt;
//
//	private Date modifiedAt;
//
//	public String getCommandID() {
//		return commandID;
//	}
//
//	public void setCommandID(String commandID) {
//		this.commandID = commandID;
//	}
//
//	public CommandStateType getCommandState() {
//		return commandState;
//	}
//
//	public void setCommandState(CommandStateType commandState) {
//		this.commandState = commandState;
//	}
//
//	public List<Map<String,ActionResult>> getActionResults() {
//		return actionResults;
//	}
//
//	public void setActionResults(List<Map<String,ActionResult>> actionResults) {
//		this.actionResults = actionResults;
//	}
//
//	public String getFiredByTriggerID() {
//		return firedByTriggerID;
//	}
//
//	public void setFiredByTriggerID(String firedByTriggerID) {
//		this.firedByTriggerID = firedByTriggerID;
//	}
//
//	public Date getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(Date createdAt) {
//		this.createdAt = createdAt;
//	}
//
//	public Date getModifiedAt() {
//		return modifiedAt;
//	}
//
//	public void setModifiedAt(Date modifiedAt) {
//		this.modifiedAt = modifiedAt;
//	}
//}
