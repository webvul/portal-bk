package com.kii.beehive.business.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.event.EventType;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.manager.ThingTagManager;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class KiiCloudEventBus {


	@Autowired
	private ApplicationContext  context;

	@Autowired
	private EventListenerDao eventDao;

	@Autowired
	private ThingTagManager thingTagManager;


	@Async
	public void onTagIDsChangeFire(List<Long> tagIDList, boolean b) {

		List<String> tags= thingTagManager.getTagNamesByIDs(tagIDList);

		tags.forEach(name->onTagChangeFire(name,b));
	}

	@Async
	public void onTagChangeFire(String tagName,boolean isAdd){



		List<EventListener> listenerList=eventDao.getEventListenerByTypeAndKey(EventType.TagChange,tagName);


		listenerList.forEach(listener->{
			String name=listener.getRelationBeanName();

			BeehiveEventProcess process= (BeehiveEventProcess) context.getBean(name);

			EventParam param = new EventParam();
			param.setParam("isAdd",isAdd);

			process.onEventFire(listener.getTargetKey(), param,listener.getCustoms());

		});

	}


	@Async
	public void onStatusUploadFire(String thingID, ThingStatus status){


		List<EventListener> listenerList=eventDao.getEventListenerByTypeAndKey(EventType.ThingStateChange,thingID);

		listenerList.forEach(listener->{

			String relationBeanName=listener.getRelationBeanName();

			BeehiveEventProcess process= (BeehiveEventProcess) context.getBean(relationBeanName);

			EventParam param=new EventParam();
			param.setParam("status",status);

			process.onEventFire(listener.getTargetKey(),param,listener.getCustoms());
		});

	}
	

}
