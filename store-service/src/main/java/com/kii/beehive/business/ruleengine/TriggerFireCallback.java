package com.kii.beehive.business.ruleengine;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.ThingNotExistException;
import com.kii.extension.ruleengine.store.trigger.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.extension.ruleengine.EventCallback;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class TriggerFireCallback implements EventCallback {


	@Autowired
	private BusinessEventBus eventBus;

	@Override
	public void onTriggerFire(String triggerID) {

		eventBus.onTriggerFire(triggerID);
	}
}
