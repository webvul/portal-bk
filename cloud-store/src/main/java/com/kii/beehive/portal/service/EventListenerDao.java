package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventType;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName="portal",appBindSource="propAppBindTool")
public class EventListenerDao extends AbstractDataAccess<EventListener>{


	public void addEventListener(EventListener listener){
		super.addEntity(listener);
	}



	public List<EventListener> getEventListenerByTypeAndKey(EventType type, String bindKey){


		QueryParam query= ConditionBuilder.andCondition()
				.equal("type",type)
				.equal("bindKeys."+bindKey,true)
				.equal("enable",true).getFinalQueryParam();

		return super.fullQuery(query);
	}

	public List<EventListener> getEventListenerByTargetKey(String targetKey) {


		QueryParam query= ConditionBuilder.andCondition()
				.equal("targetKey",targetKey)
				.equal("enable",true).getFinalQueryParam();

		return super.fullQuery(query);
	}

	@Override
	protected Class<EventListener> getTypeCls() {
		return EventListener.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("eventListener");
	}


	public void disableListener(String id) {

		Map<String,Object> param= Collections.singletonMap("enable",false);

		super.updateEntity(param,id);
	}


	public void enableListener(String id) {

		Map<String,Object> param= Collections.singletonMap("enable",true);

		super.updateEntity(param,id);

	}
}
