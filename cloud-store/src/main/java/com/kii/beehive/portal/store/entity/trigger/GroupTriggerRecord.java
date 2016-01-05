package com.kii.beehive.portal.store.entity.trigger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupTriggerRecord extends TriggerRecord{

	private TriggerSource  source;

	private TriggerGroupPolicy  policy;


	public TriggerGroupPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(TriggerGroupPolicy policy) {
		this.policy = policy;
	}

	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}

}
