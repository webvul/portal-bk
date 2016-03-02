package com.kii.extension.store.trigger;

import java.util.ArrayList;
import java.util.List;

public class SummarySource {

	private List<SummaryExpress> expressList=new ArrayList<>();

	private TriggerSource  source;

	public List<SummaryExpress> getExpressList() {
		return expressList;
	}

	public void setExpressList(List<SummaryExpress> expressList) {
		this.expressList = expressList;
	}

	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}


}
