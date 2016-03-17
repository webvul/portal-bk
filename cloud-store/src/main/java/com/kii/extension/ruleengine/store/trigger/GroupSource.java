package com.kii.extension.ruleengine.store.trigger;

public class GroupSource implements  SourceElement{


	private TriggerSource  source;

	private TriggerGroupPolicy  policy;

	private String  express;

	private Condition condition;

	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}

	public TriggerGroupPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(TriggerGroupPolicy policy) {
		this.policy = policy;
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
