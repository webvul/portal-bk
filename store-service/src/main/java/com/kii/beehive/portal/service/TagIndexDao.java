package com.kii.beehive.portal.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
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

	public void removeThingFromTag(String tagID,List<String> thingIDs,List<String> appIDs){

		TagIndex  tagIdx=super.getObjectByID(tagID);


		Set<String> currThings=tagIdx.getGlobalThings();
		currThings.removeAll(thingIDs);
		
		Set<String> curraAppIDs=tagIdx.getAppIDs();
		curraAppIDs.removeAll(appIDs);

		TagIndex update=new TagIndex();
		update.setAppIDs(curraAppIDs);
		update.setGlobalThings(currThings);

		super.updateEntityWithVersion(update,tagID,tagIdx.getVersion());

	}

	public TagIndex getTagIndexByID(String id){
		return super.getObjectByID(id);
	}

	public void addTagIndex(TagIndex tag) {

		super.addKiiEntity(tag);
	}

	public List<TagIndex> getTagsByIDs(String[] ids){
		return super.getEntitys(ids);
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
