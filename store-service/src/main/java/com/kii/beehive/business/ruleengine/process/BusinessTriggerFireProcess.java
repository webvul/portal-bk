//package com.kii.beehive.business.ruleengine.process;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.kii.beehive.business.event.BusinessEventListenerService;
//import com.kii.beehive.business.event.impl.TriggerFireProcess;
//import com.kii.beehive.business.service.BusinessTriggerService;
//import com.kii.beehive.portal.event.EventListener;
//import com.kii.beehive.business.manager.SimpleThingTriggerManager;
//import com.kii.beehive.business.manager.ThingGroupStateManager;
//import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
//import com.kii.beehive.portal.store.entity.trigger.BusinessTrigger;
//import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
//import com.kii.extension.sdk.entity.thingif.TriggerWhen;
//
//@Component(BusinessEventListenerService.FIRE_TRIGGER_WHEN_MATCH)
//public class BusinessTriggerFireProcess  implements TriggerFireProcess {
//
//
//	@Autowired
//	private SimpleThingTriggerManager simpleTriggerManager;
//
//	@Autowired
//	private ThingGroupStateManager  groupTriggerManager;
//
//
//	@Autowired
//	private TriggerRuntimeStatusDao statusDao;
//
//
//	@Autowired
//	private BusinessTriggerService  triggerService;
//
//	@Autowired
//	private BusinessEventListenerService listenerService;
//
//	@Override
//	public void onEventFire(EventListener listener, String thingID,TriggerWhen when,boolean sign) {
//
//
//		BusinessTrigger  trigger=triggerService.getTriggerByID(listener.getBindKeys().keySet().iterator().next());
//
//		String triggerID=listener.getTargetKey();
//
//		if(!trigger.getTargetID().equals(triggerID)){
//
//			listenerService.disableTrigger(listener.getId());
//
//			return;
//		}
//
//
////		BeehiveTriggerType type= (BeehiveTriggerType) listener.getCustoms().get(BusinessEventListenerService.TRIGGER_TYPE);
//
//		TriggerRuntimeState state=statusDao.getObjByID(triggerID);
//
//		if(state==null ||  !state.getThingIDSet().contains(thingID)){
//
//			listenerService.disableTrigger(listener.getId());
//
//			return;
//		}
//
//		switch(state.getType()){
//
//			case Simple:
//				if(sign) {
//					simpleTriggerManager.onConditionMatch(thingID, triggerID);
//				}
//				break;
//			case Group:
//
//				groupTriggerManager.onStatusChange(thingID,triggerID);
//
//				break;
//			default:
//				throw new IllegalArgumentException();
//		}
//	}
//}
