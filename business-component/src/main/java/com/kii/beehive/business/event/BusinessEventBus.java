package com.kii.beehive.business.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.business.event.impl.ThingStatusChangeProcess;
import com.kii.beehive.business.event.impl.TriggerFireProcess;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventType;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class BusinessEventBus {


	@Autowired
	private ApplicationContext  context;

	@Autowired
	private EventListenerDao eventDao;


	@Async
	public void onTriggerFire(String triggerID){


		List<EventListener> listeners=eventDao.getEventListenerByTypeAndKey(EventType.TriggerFire,triggerID);

		listeners.forEach(listener->{

			String name=listener.getRelationBeanName();

			TriggerFireProcess process= (TriggerFireProcess) context.getBean(name);

			process.onEventFire(listener,triggerID);

		});

	}

	@Async
	public void onTagChangeFire(String tagName,boolean isAdd){



		List<EventListener> listenerList=eventDao.getEventListenerByTypeAndKey(EventType.TagChange,tagName);


		listenerList.forEach(listener->{
			String name=listener.getRelationBeanName();

			TagChangeProcess process= (TagChangeProcess) context.getBean(name);

			process.onEventFire(listener);

		});

	}


	@Async
	public void onStatusUploadFire(String thingID, ThingStatus status){


		List<EventListener> listenerList=eventDao.getEventListenerByTypeAndKey(EventType.ThingStateChange,thingID);

		listenerList.forEach(listener->{

			String relationBeanName=listener.getRelationBeanName();

			ThingStatusChangeProcess process= (ThingStatusChangeProcess) context.getBean(relationBeanName);

			process.onEventFire(listener,status,thingID);
		});

	}
	

}