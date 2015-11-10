package com.kii.beehive.portal.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
public class TagIndexDao extends AbstractDataAccess<TagIndex> {




	public void addThingToTag(TagIndex tagIdx,List<GlobalThingInfo> things){

		System.out.println(things.get(0).getKiiAppID());
		Set<String> newThings=things.stream().map(GlobalThingInfo::getId).collect(Collectors.toSet());

		Set<String> newApps=things.stream().map(GlobalThingInfo::getKiiAppID).collect(Collectors.toSet());

		Set<String> thingIDs=tagIdx.getGlobalThings();
		thingIDs.addAll(newThings);

		Set<String> appIDs=tagIdx.getKiiAppIDs();
		appIDs.addAll(newApps);

		TagIndex update=new TagIndex();
		System.out.println(appIDs+"===="+thingIDs);
		update.setKiiAppIDs(appIDs);
		update.setGlobalThings(thingIDs);

		super.updateEntityWithVersion(update,tagIdx.getId(),tagIdx.getVersion());

	}

	public void removeThingFromTag(String tagID,List<String> thingIDs,List<String> appIDs){

		TagIndex  tagIdx=super.getObjectByID(tagID);


		Set<String> currThings=tagIdx.getGlobalThings();
		currThings.removeAll(thingIDs);
		
		Set<String> curraAppIDs=tagIdx.getKiiAppIDs();
		curraAppIDs.removeAll(appIDs);

		TagIndex update=new TagIndex();
		update.setKiiAppIDs(curraAppIDs);
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
