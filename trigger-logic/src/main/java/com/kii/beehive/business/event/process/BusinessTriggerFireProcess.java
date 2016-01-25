package com.kii.beehive.business.event.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventProcess;
import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.service.TriggerFireCallbackService;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;

@Component(BusinessEventListenerService.FIRE_TRIGGER_WHEN_MATCH)
public class BusinessTriggerFireProcess  implements BusinessEventProcess {


	@Autowired
	private TriggerFireCallbackService callbackService;

	@Override
	public void onEventFire(EventListener listener, EventParam param) {

/*
			param.setParam("thingID",thingID);
			param.setParam("triggerWhen",when);
 */
		String thingID= (String) param.getParam("thingID");

		String triggerID=listener.getTargetKey();

		TriggerWhen when= (TriggerWhen) param.getParam("triggerWhen");

		BeehiveTriggerType type= (BeehiveTriggerType) listener.getCustoms().get("triggerType");


		switch(type){


			case Simple:
				callbackService.onSimpleArrive(thingID,triggerID);
				break;
			case Group:
				if(when==TriggerWhen.CONDITION_FALSE_TO_TRUE){
					callbackService.onPositiveArrive(thingID,triggerID);
				}else if(when==TriggerWhen.CONDITION_TRUE_TO_FALSE){
					callbackService.onNegativeArrive(thingID,triggerID);
				}
				break;
			default:
				throw new IllegalArgumentException();
		}
	}
}
