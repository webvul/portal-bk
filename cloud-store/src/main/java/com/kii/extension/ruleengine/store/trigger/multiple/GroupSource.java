package com.kii.extension.ruleengine.store.trigger.multiple;

import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.TriggerSource;

public class GroupSource implements SourceElement {


	private TriggerSource source;

	private String  express;

	private Condition condition;

	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public SourceElementType getType() {
		return SourceElementType.group;
	}
}
