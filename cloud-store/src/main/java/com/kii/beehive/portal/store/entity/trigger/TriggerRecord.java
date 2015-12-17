package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.List;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class TriggerRecord extends KiiEntity {


	private TriggerSource  source;

	private StatePredicate  perdicate;

	private TriggerTarget  target;

	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}

	public StatePredicate getPerdicate() {
		return perdicate;
	}

	public void setPerdicate(StatePredicate perdicate) {
		this.perdicate = perdicate;
	}

	public TriggerTarget getTarget() {
		return target;
	}

	public void setTarget(TriggerTarget target) {
		this.target = target;
	}
}
