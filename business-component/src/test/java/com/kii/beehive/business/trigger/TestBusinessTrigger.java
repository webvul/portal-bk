//package com.kii.beehive.business.trigger;
//
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.kii.beehive.business.TestInit;
//import com.kii.beehive.business.event.BusinessEventBus;
//import com.kii.beehive.business.service.BusinessTriggerService;
//import com.kii.beehive.business.service.ThingIFInAppService;
//import StatePredicate;
//import ThingStatus;
//import TriggerWhen;
//import Condition;
//import ConditionBuilder;
//
//
//public class TestBusinessTrigger extends TestInit {
//
//	@Autowired
//	private BusinessEventBus  eventBus;
//
//	@Autowired
//	private BusinessTriggerService  service;
//
//	@Autowired
//	private ThingIFInAppService  thingIFService;
//
//	String[] ids={"0af7a7e7-th.f83120e36100-a269-5e11-e5bb-0bc2e136","c1744915-th.aba700e36100-4558-5e11-6d8b-053cc8e8"};
//
//	@Test
//	public void testCreate(){
//
//
//		List<String> thingIDs=new ArrayList();
//		thingIDs.addAll(Arrays.asList(ids));
//
//		StatePredicate predicate=new StatePredicate();
//		predicate.setTriggersWhen(TriggerWhen.CONDITION_FALSE_TO_TRUE);
//
//		Condition cond= ConditionBuilder.andCondition().greatAndEq("foo",100).lessAndEq("bar",0).getConditionInstance();
//
//		predicate.setCondition(cond);
//
//		service.registerBusinessTrigger(thingIDs,"threaddemo",predicate);
//
//
//
//	}
//
//	@Test
//	public void sendStatus() throws IOException {
//
//		ThingStatus status=new ThingStatus();
//		status.setField("foo",150);
//		status.setField("bar",-30);
//
//		eventBus.onStatusUploadFire(ids[0],status);
//
//		System.in.read();
//
////		thingIFService.putStatus(ids[0],status);
//
//	}
//}
