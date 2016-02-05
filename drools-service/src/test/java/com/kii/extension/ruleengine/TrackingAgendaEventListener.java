package com.kii.extension.ruleengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

	private static Logger log = LoggerFactory.getLogger(TrackingAgendaEventListener.class);

	private List<String> activationList = new ArrayList<String>();

	@Override
	public void afterMatchFired(AfterMatchFiredEvent event) {
		Rule rule = event.getMatch().getRule();

		String ruleName = rule.getName();
		Map<String, Object> ruleMetaDataMap = rule.getMetaData();

		activationList.add(ruleName);
		StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

		if (ruleMetaDataMap.size() > 0) {
			sb.append("\n  With [" + ruleMetaDataMap.size() + "] meta-data:");
			for (String key : ruleMetaDataMap.keySet()) {
				sb.append("\n    key=" + key + ", value="
						+ ruleMetaDataMap.get(key));
			}
		}

		log.debug(sb.toString());
	}

	public boolean isRuleFired(String ruleName) {
		for (String a : activationList) {
			if (a.equals(ruleName)) {
				return true;
			}
		}
		return false;
	}

	public void reset() {
		activationList.clear();
	}

	public final List<String> getActivationList() {
		return activationList;
	}

	public String activationsToString() {
		if (activationList.size() == 0) {
			return "No activations occurred.";
		} else {
			StringBuilder sb = new StringBuilder("Activations: ");
			for (String activation : activationList) {
				sb.append("\n  rule: ").append(activation);
			}
			return sb.toString();
		}
	}

}
