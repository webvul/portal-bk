package com.kii.beehive.portal.store.test;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventType;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class TestTriggerRecordDao extends TestTemplate {


	@Autowired
	private TriggerRecordDao triggerRecordDao;


	@Test
	public void testQuery(){

//		for (int i = 0; i < 100; i++) {
//
//			SimpleTriggerRecord record = new SimpleTriggerRecord();
//			record.setRecordStatus(TriggerRecord.StatusType.enable);
//			record.setTriggerName("test" + i);
//			triggerRecordDao.addEntity(record);
//		}
//

		List<TriggerRecord> list=triggerRecordDao.getAllEnableTrigger();

		System.out.println(list.size());
//		assertEquals(50,list.size());





	}

}
