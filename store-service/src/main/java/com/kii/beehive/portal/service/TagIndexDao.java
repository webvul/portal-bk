package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
public class TagIndexDao extends AbstractDataAccess<TagIndex> {




	public void addThingToTag(TagIndex tagIdx,List<GlobalThingInfo> things){


		Set<String> newThings=things.stream().map(GlobalThingInfo::getId).collect(Collectors.toSet());

		Set<String> newApps=things.stream().map(GlobalThingInfo::getAppID).collect(Collectors.toSet());

		Set<String> thingIDs=tagIdx.getGlobalThings();
		thingIDs.addAll(newThings);

		Set<String> appIDs=tagIdx.getAppIDs();
		appIDs.addAll(newApps);

		TagIndex update=new TagIndex();
		update.setAppIDs(appIDs);
		update.setGlobalThings(thingIDs);

		super.updateEntityWithVersion(update,tagIdx.getId(),tagIdx.getVersion());

	}

	public void removeThingFromTag(String tagID,List<String> thingIDs){

		TagIndex  tagIdx=super.getObjectByID(tagID);


		Set<String> currThings=tagIdx.getGlobalThings();
		currThings.removeAll(thingIDs);

		TagIndex update=new TagIndex();
		update.setGlobalThings(currThings);

		super.updateEntityWithVersion(update,tagID,tagIdx.getVersion());

	}

	@Override
	protected Class<TagIndex> getTypeCls() {
		return TagIndex.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("TagThingInfo");
	}
}
