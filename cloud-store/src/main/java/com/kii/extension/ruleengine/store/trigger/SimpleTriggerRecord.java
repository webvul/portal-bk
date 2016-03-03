package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SimpleTriggerRecord extends TriggerRecord{

	public SimpleTriggerRecord(){

	}

	private ThingID source;



	public ThingID getSource() {
		return source;
	}

	public void setSource(ThingID source) {
		this.source = source;
	}

	@JsonIgnore
	public void setThingID(long thingID){
		this.source=new ThingID();
		source.setThingID(thingID);
	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Simple;
	}

//	public TriggerType getType() {
//		return TriggerType.Simple;
//	}

	public static class ThingID{

		long thingID;

		public long getThingID() {
			return thingID;
		}

		public void setThingID(long thingID) {
			this.thingID = thingID;
		}
	}
}
