package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SummarySource  {

	private List<SummaryExpress> expressList=new ArrayList<>();

	private TagSelector source;

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

	public TagSelector getSource() {
		return source;
	}



	public void setSource(TagSelector source) {
		this.source = source;
	}

}
