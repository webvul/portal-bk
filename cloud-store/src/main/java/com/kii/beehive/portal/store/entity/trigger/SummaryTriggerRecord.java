package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.List;

public class SummaryTriggerRecord extends TriggerRecord {

	private List<SummarySource> summarySource=new ArrayList<>();

	public List<SummarySource> getSummarySource() {
		return summarySource;
	}

	public void setSummarySource(List<SummarySource> summarySource) {
		this.summarySource = summarySource;
	}
}
