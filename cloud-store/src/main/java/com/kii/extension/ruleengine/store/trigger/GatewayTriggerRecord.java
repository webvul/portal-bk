package com.kii.extension.ruleengine.store.trigger;


public class GatewayTriggerRecord extends  TriggerRecord{



	private String gatewayVendorThingID;

	private String gatewayFullKiiThingID;

	private VenderThingList  source=new VenderThingList();

//	private TriggerGroupPolicy  policy;


//	public TriggerGroupPolicy getPolicy() {
//		return policy;
//	}
//
//	public void setPolicy(TriggerGroupPolicy policy) {
//		this.policy = policy;
//	}


	public VenderThingList getSource() {
		return source;
	}

	public void setSource(VenderThingList source) {
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
