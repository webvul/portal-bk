package com.kii.extension.ruleengine.store.trigger.multiple;

import java.util.HashMap;
import java.util.Map;

import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

public class MultipleSrcTriggerRecord extends TriggerRecord {


	private Map<String,SourceElement> summarySource=new HashMap<>();


	public Map<String, SourceElement> getSummarySource() {
		return summarySource;
	}



	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Multiple;
	}
}
