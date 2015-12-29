package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryTriggerRecord extends TriggerRecord {

	private List<SummaryExpress> summaryExpress=new ArrayList<>();

	private TriggerSource  source;





	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}

	public TriggerType getType() {
		return TriggerType.Summary;
	}

	public List<SummaryExpress> getSummaryExpress() {
		return summaryExpress;
	}

	public void setSummaryExpress(List<SummaryExpress> summaryExpress) {
		this.summaryExpress = summaryExpress;
	}


}
