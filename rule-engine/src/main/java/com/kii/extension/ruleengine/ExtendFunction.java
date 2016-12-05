package com.kii.extension.ruleengine;

import java.util.Map;

public interface ExtendFunction {


	public Object callFunction(String namespace,String  functionName,TriggerContext context);


	public static class  TriggerContext{


		private String triggerID;

		private String  currThingID;

		private Map<String,Object> values;


		public String getTriggerID() {
			return triggerID;
		}

		public void setTriggerID(String triggerID) {
			this.triggerID = triggerID;
		}

		public String getCurrThingID() {
			return currThingID;
		}

		public void setCurrThingID(String currThingID) {
			this.currThingID = currThingID;
		}

		public Map<String, Object> getValues() {
			return values;
		}

		public void setValues(Map<String, Object> values) {
			this.values = values;
		}
	}

}
