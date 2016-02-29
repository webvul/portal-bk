//package com.kii.beehive.portal.ruleengine.process;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.kii.beehive.business.event.BusinessEventListenerService;
//import com.kii.beehive.business.event.impl.ThingStatusChangeProcess;
//import com.kii.beehive.portal.event.EventListener;
//import com.kii.beehive.business.manager.ThingStateSummaryManager;
//import com.kii.beehive.portal.service.TriggerRecordDao;
//import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
//import com.kii.extension.sdk.entity.thingif.ThingStatus;
//
//@Component(BusinessEventListenerService.COMPUTE_SUMMARY_STATE)
//public class ComputeSummaryStateProcess implements ThingStatusChangeProcess {
//
//
//	@Autowired
//	private ThingStateSummaryManager summaryService;
//
//	@Autowired
//	private TriggerRecordDao triggerDao;
//
//
//	@Autowired
//	private BusinessEventListenerService listenerService;
//
//
//	@Override
//	public void onEventFire(EventListener listener, ThingStatus status,String thingID) {
//
//
//		String groupID= (String) listener.getCustoms().get(BusinessEventListenerService.GROUP_NAME);
//
//		String triggerID=listener.getTargetKey();
//
//		SummaryTriggerRecord trigger= (SummaryTriggerRecord) triggerDao.getTriggerRecord(triggerID);
//
//		if(trigger==null){
//			listenerService.disableTrigger(listener.getId());
//			return;
//		}
//
//		summaryService.computeStateSummary(trigger,groupID,status);
//
//
//	}
//}
