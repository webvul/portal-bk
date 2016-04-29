package com.kii.extension.ruleengine.store.trigger.multiple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.TriggerSource;

public class GroupSummarySource implements SourceElement{


	private String stateName="NULL_STATE";

	private SummaryFunctionType function;

	private TriggerSource source;


	@JsonUnwrapped
	private Express express=new Express();


	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	@JsonIgnore
	public Condition getCondition() {
		return express.getCondition();
	}

	public void setCondition(Condition condition) {
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

	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}

	@Override
	public SourceElement.SourceElementType getType() {
		return SourceElement.SourceElementType.summary;
	}

}
