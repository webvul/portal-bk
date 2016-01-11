package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.List;

public class SummarySource {

	private SummaryExpress summaryExpress;

	private TriggerSource  source;

	public SummaryExpress getSummaryExpress() {
		return summaryExpress;
	}

	public void setSummaryExpress(SummaryExpress summaryExpress) {
		this.summaryExpress = summaryExpress;
	}

	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}
}
