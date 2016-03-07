package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventType;
import com.kii.beehive.portal.service.EventListenerDao;

public class TestEventDao extends TestTemplate {


	@Autowired
	private EventListenerDao   eventDao;


	@Test
	public void testQuery(){


		EventListener  listener=new EventListener();

		listener.addBindKey("abc.xyz");
		listener.setTargetKey("foo");
		listener.setEnable(true);
		listener.setType(EventType.ThingStateChange);

//		eventDao.addEventListener(listener);

		Collection<EventListener> list=eventDao.getEventListenerByTypeAndKey(EventType.ThingStateChange,"abc.xyz");

		assertEquals(1,list.size());


		assertEquals("foo",list.iterator().next().getTargetKey());

		list=eventDao.getEventListenerByTypeAndKey(EventType.ThingStateChange,"c1744915-th.f83120e36100-a269-5e11-bf4b-0c5b4813");
		assertEquals(3,list.size());



	}

}
