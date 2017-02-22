package com.kii.beehive.portal.store.entity.trigger;

import java.util.HashMap;
import java.util.Map;

public class MultipleSrcTriggerRecord extends TriggerRecord {


	private Map<String,SourceElement> summarySource=new HashMap<>();


	public Map<String, SourceElement> getSummarySource() {
		return summarySource;
	}



	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Multiple;
	}
	
	public void addSource(String name,SourceElement elem) {
		summarySource.put(name,elem);
	}
}
