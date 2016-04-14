package com.kii.extension.ruleengine.store.trigger.multiple;

import com.kii.extension.ruleengine.store.trigger.SlideFuntion;
import com.kii.extension.ruleengine.store.trigger.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.TriggerSource;

public class SummaryFunSource implements SourceElement{


	private String stateName;

	private SummaryFunctionType function;

	private SlideFuntion slideFuntion;

	private TriggerSource source;


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

	public SlideFuntion getSlideFuntion() {
		return slideFuntion;
	}

	public void setSlideFuntion(SlideFuntion slideFuntion) {
		this.slideFuntion = slideFuntion;
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
