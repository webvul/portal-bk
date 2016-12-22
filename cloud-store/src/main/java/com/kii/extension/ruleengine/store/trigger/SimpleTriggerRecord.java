package com.kii.extension.ruleengine.store.trigger;

public class SimpleTriggerRecord extends TriggerRecord{

	public SimpleTriggerRecord(){

	}

	private ThingSource source;



	public ThingSource getSource() {
		return source;
	}

	public void setSource(ThingSource source) {
		this.source = source;
	}

//	@JsonIgnore
//	public void setThingID(long thingID){
//		this.source=new ThingID();
//		source.setThingID(thingID);
//	}
//	@JsonIgnore
//	public void setVendorThingID(String vendorThingID){
//		if(source != null) {
//			this.source=new ThingID();
//		}
//		source.setVendorThingID(vendorThingID);
//	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Simple;
	}

//	public TriggerType getType() {
//		return TriggerType.Simple;
//	}

//	public static class ThingID{
//
//		long thingID;
//		String vendorThingID;
//
//		public String getVendorThingID() {
//			return vendorThingID;
//		}
//
//		public void setVendorThingID(String vendorThingID) {
//			this.vendorThingID = vendorThingID;
//		}
//		public long getThingID() {
//			return thingID;
//		}
//
//		public void setThingID(long thingID) {
//			this.thingID = thingID;
//		}
//	}
}
