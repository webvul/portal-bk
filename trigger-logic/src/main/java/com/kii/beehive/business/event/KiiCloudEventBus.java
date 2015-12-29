package com.kii.beehive.business.event;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.event.EventType;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class KiiCloudEventBus {


	@Autowired
	private ApplicationContext  context;

	@Autowired
	private EventListenerDao eventDao;

	@Async
	public void onTagChangeFire(String tagName,Collection<String> relationThingID){



		List<EventListener> listenerList=eventDao.getEventListenerByTypeAndKey(EventType.TagChange,tagName);


		listenerList.forEach(listener->{

			String name=listener.getRelationBeanName();

			BeehiveEventProcess process=context.getBean(BeehiveEventProcess.class,name);

			EventParam param = new EventParam();
			param.setParamMap("thingIDs", relationThingID);

			process.onEventFire(listener.getTargetKey(), param);

		});

	}


	@Async
	public void onStatusUploadFire(String thingID, ThingStatus status){


		List<EventListener> listenerList=eventDao.getEventListenerByTypeAndKey(EventType.ThingStateChange,thingID);

		listenerList.forEach(listener->{

			String relationBeanName=listener.getRelationBeanName();

			BeehiveEventProcess process=context.getBean(relationBeanName,BeehiveEventProcess.class);

			EventParam param=new EventParam();
			param.setParamMap("status",status);

			process.onEventFire(listener.getTargetKey(),param);
		});

	}


}
