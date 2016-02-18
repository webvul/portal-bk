package com.kii.extension.ruleengine.drools;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

	private static Logger log = LoggerFactory.getLogger(TrackingAgendaEventListener.class);


	@Override
	public void afterMatchFired(AfterMatchFiredEvent event) {
		Rule rule = event.getMatch().getRule();

		String ruleName = rule.getName();

		StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

		for(Object obj:event.getMatch().getObjects()){

			sb.append("obj name:"+obj.getClass().getName()+" value:"+obj.toString());

		}


		log.info(sb.toString());
	}


	public void beforeMatchFired(BeforeMatchFiredEvent event) {
		log.info("before fire:"+event.getMatch().getRule().getName());

	}

	public void matchCancelled(MatchCancelledEvent event) {
		log.info("match cancell:"+event.getMatch().getRule().getName());

		log.info("cause:"+event.getCause().name());
	}


	public void matchCreated(MatchCreatedEvent event) {
		log.info("match create:"+event.getMatch().getRule().getName());
	}


}
