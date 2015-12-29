package com.kii.beehive.portal.store.entity.trigger;

public class SimpleTriggerRecord extends TriggerRecord{


	private ThingID source;

	public ThingID getSource() {
		return source;
	}

	public void setSource(ThingID source) {
		this.source = source;
	}

	public TriggerType getType() {
		return TriggerType.Simple;
	}

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
