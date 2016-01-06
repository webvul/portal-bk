package com.kii.beehive.portal.store.entity.trigger;

public class SummaryTriggerRuntimeState extends TriggerRuntimeState {


	private String listenerID;

	private String summaryThingID;

	public String getListenerID() {
		return listenerID;
	}

	public void setListenerID(String listenerID) {
		this.listenerID = listenerID;
	}

	public String getSummaryThingID() {
		return summaryThingID;
	}

	public void setSummaryThingID(String summaryThingID) {
		this.summaryThingID = summaryThingID;
	}

}
