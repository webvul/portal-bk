package com.kii.beehive.business.ruleengine.entitys;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.Express;

public class GroupSummaryEngineSource implements EngineSourceElement {


	private String stateName="NULL_STATE";

	private SummaryFunctionType function;

	private ObjectCollectSource source;


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

	public ObjectCollectSource getSource() {
		return source;
	}

	public void setSource(ObjectCollectSource source) {
		this.source = source;
	}

	@Override
	public EngineSourceElement.SourceElementType getType() {
		return EngineSourceElement.SourceElementType.summary;
	}
	
	public enum SummaryFunctionType {
		
		sum,count,max,min,average,objCol;
		
		
	}

}
