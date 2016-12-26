package com.kii.extension.ruleengine.store.trigger;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.kii.extension.ruleengine.store.trigger.groups.SummarySource;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThingInGW;

public class GatewayTriggerRecord extends TriggerRecord {

	private Map<String,GatewaySummarySource> summarySource=new HashMap<>();


	private String gatewayVendorThingID;

	private String gatewayFullKiiThingID;


	public Map<String, GatewaySummarySource> getSummarySource() {
		return summarySource;
	}

	public void setSummarySource(Map<String, GatewaySummarySource> summarySource) {
		this.summarySource = summarySource;
	}
	public String getGatewayVendorThingID() {
		return gatewayVendorThingID;
	}

	public void setGatewayVendorThingID(String gatewayVendorThingID) {
		this.gatewayVendorThingID = gatewayVendorThingID;
	}

	public String getGatewayFullKiiThingID() {
		return gatewayFullKiiThingID;
	}

	public void setGatewayFullKiiThingID(String gatewayFullKiiThingID) {
		this.gatewayFullKiiThingID = gatewayFullKiiThingID;
	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Gateway;
	}
	
	
	public SummaryTriggerRecord  getSummaryTriggerInstance(){
		SummaryTriggerRecord inSummaryTriggerRecord = new SummaryTriggerRecord();
		//convert gate trigger to summary
		BeanUtils.copyProperties(this, inSummaryTriggerRecord, "summarySource", "targets");
		this.getSummarySource().forEach((key, gatewaySummarySource) -> {
			SummarySource summarySource = new SummarySource();
			BeanUtils.copyProperties(gatewaySummarySource, summarySource);
			inSummaryTriggerRecord.getSummarySource().put(key, summarySource);
		});
		this.getTargets().forEach((executeTarget) -> {
			CommandToThingInGW commandToThingInGW = (CommandToThingInGW) executeTarget;
			CommandToThing commandToThing = new CommandToThing();
			BeanUtils.copyProperties(commandToThingInGW, commandToThing);
			commandToThing.setSelector(new TagSelector());
			BeanUtils.copyProperties(commandToThingInGW.getSelector(), commandToThing.getSelector());
			inSummaryTriggerRecord.getTargets().add(commandToThing);
		});
		
		return inSummaryTriggerRecord;
	}
}
