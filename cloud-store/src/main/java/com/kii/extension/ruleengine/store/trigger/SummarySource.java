package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SummarySource {

	private List<SummaryExpress> expressList=new ArrayList<>();

	private TriggerSource  source;

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

	public TriggerSource getSource() {
		return source;
	}


	@JsonIgnore
	public void setSourceSelector(TagSelector  selector){
		TriggerSource source=new TriggerSource();
		source.setSelector(selector);
		this.source=source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}


}
