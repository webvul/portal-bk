package com.kii.beehive.portal.store.entity.trigger.groups;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SummaryExpress {

	private String stateName;

	private SummaryFunctionType function;

	private String summaryAlias;

	private SlideFuntion slideFuntion;

	@JsonIgnore
	public String getFullSlideFunName(){

		return function.name()+"-"+slideFuntion.getType().name();

	}

	public SlideFuntion getSlideFuntion() {
		return slideFuntion;
	}

	public void setSlideFuntion(SlideFuntion slideFuntion) {
		this.slideFuntion = slideFuntion;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public SummaryFunctionType getFunction() {
		return function;
	}

	public void setFunction(SummaryFunctionType function) {
		this.function = function;
	}



	public String getSummaryAlias() {
		return summaryAlias;
	}

	public void setSummaryAlias(String summaryAlias) {
		this.summaryAlias = summaryAlias;
	}




}
