package com.kii.beehive.portal.event;

import java.lang.annotation.Annotation;

import com.kii.beehive.portal.event.annotation.TagChanged;
import com.kii.beehive.portal.event.annotation.ThingStateChange;

public enum EventType {

	TagChange(TagChanged.class),ThingStateChange(ThingStateChange.class);

	private Class<? extends Annotation> annotation;

	EventType(Class<? extends Annotation> cls){
		this.annotation=cls;
	}

	public Class<? extends Annotation> getAnnotation(){
		return annotation;
	}
}
