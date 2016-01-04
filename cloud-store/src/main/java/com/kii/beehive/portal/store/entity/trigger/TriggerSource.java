package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerSource {

	private List<Long> thingList=new ArrayList<>();

	private List<String> tagList=new ArrayList<>();

	private boolean isAndExpress=false;

//	private Map<String,Boolean> relationTag=new HashMap<>();


	public List<String> getTagList() {
		return tagList;
	}

	public void setTagCollect(List<String> tagCollect) {
		this.tagList = tagCollect;
	}

	public boolean isAndExpress() {
		return isAndExpress;
	}

	public void setAndExpress(boolean andExpress) {
		isAndExpress = andExpress;
	}


	public List<Long> getThingList() {
		return thingList;
	}

	public void setThingList(List<Long> thingList) {
		this.thingList = thingList;
	}

//	public Map<String, Boolean> getRelationTag() {
//		return relationTag;
//	}
//
//	public void setRelationTag(Map<String, Boolean> relationTag) {
//		this.relationTag = relationTag;
//	}
}
