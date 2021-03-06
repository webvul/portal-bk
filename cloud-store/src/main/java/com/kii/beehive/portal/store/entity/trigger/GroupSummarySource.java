package com.kii.beehive.portal.store.entity.trigger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.store.entity.trigger.groups.SummaryFunctionType;

public class GroupSummarySource implements SourceElement{


	private String stateName="NULL_STATE";

	private SummaryFunctionType function;

	private ThingCollectSource source;


	@JsonUnwrapped
	private Express express=new Express();
	
	


	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public void setTheCondition(Condition condition) {
		this.express.setCondition(condition);
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

	public ThingCollectSource getSource() {
		return source;
	}

	public void setSource(ThingCollectSource source) {
		this.source = source;
	}

	@Override
	public SourceElement.SourceElementType getType() {
		return SourceElement.SourceElementType.summary;
	}

}
