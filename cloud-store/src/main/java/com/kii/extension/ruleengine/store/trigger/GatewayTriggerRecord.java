package com.kii.extension.ruleengine.store.trigger;


import java.util.HashMap;
import java.util.Map;

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
}
