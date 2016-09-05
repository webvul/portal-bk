package com.kii.extension.ruleengine.store.trigger;


public class GatewayTriggerRecord extends  SummaryTriggerRecord{



	private String gatewayVendorThingID;

	private String gatewayFullKiiThingID;

	private VendorThingList source=new VendorThingList();


	public VendorThingList getSource() {
		return source;
	}

	public void setSource(VendorThingList source) {
		this.source = source;
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
