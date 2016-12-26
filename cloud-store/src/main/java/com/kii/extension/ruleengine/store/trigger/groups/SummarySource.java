package com.kii.extension.ruleengine.store.trigger.groups;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.ruleengine.store.trigger.ThingCollectSource;

public class SummarySource  {

	private List<SummaryExpress> expressList=new ArrayList<>();

	private ThingCollectSource source;

	public List<SummaryExpress> getExpressList() {
		return expressList;
	}

	@JsonIgnore
	public void addExpress(SummaryExpress express){
		this.expressList.add(express);
	}

	public void setExpressList(List<SummaryExpress> expressList) {
		this.expressList = expressList;
	}

	public ThingCollectSource getSource() {
		return source;
	}



	public void setSource(ThingCollectSource source) {
		this.source = source;
	}

}
