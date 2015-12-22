package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.List;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class TriggerRecord extends KiiEntity {


	private TriggerSource  source;

	private List<SummaryExpress>  summaryExpress=new ArrayList<>();

	private StatePredicate  perdicate;

	private List<TriggerTarget>  targets=new ArrayList<>();

	public List<SummaryExpress> getSummaryExpress() {
		return summaryExpress;
	}

	public void setSummaryExpress(List<SummaryExpress> summarExpress) {
		this.summaryExpress = summarExpress;
	}

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

	public List<TriggerTarget> getTargets() {
		return targets;
	}

	public void setTarget(List<TriggerTarget> target) {
		this.targets = target;
	}
}
