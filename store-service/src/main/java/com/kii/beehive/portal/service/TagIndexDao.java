package com.kii.beehive.portal.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
@Component
public class TagIndexDao extends AbstractDataAccess<TagIndex> {
	
	private final String BUCKET_INFO = "TagThingInfo";
	
	public List<TagIndex> getAllTag() {
		return super.query(ConditionBuilder.getAll().getFinalCondition().build());
	}
	
	public List<TagIndex> findTagIndexByTagNameArray(String[] tagNameArray){
		QueryParam query = ConditionBuilder.orCondition().In("_id", tagNameArray).getFinalCondition().build();
		List<TagIndex> tagIndexList = super.query(query);
		return tagIndexList;
	}
	
	public void removeTagByID(String id){
		super.removeEntity(id);
	}
	
	public void addThingToTag(TagIndex tagIdx,List<GlobalThingInfo> things){

		Set<String> newThings=things.stream().map(GlobalThingInfo::getId).collect(Collectors.toSet());

		Set<String> newApps=things.stream().map(GlobalThingInfo::getKiiAppID).collect(Collectors.toSet());

		Set<String> thingIDs=tagIdx.getGlobalThings();
		thingIDs.addAll(newThings);

		Set<String> appIDs=tagIdx.getKiiAppIDs();
		appIDs.addAll(newApps);

		TagIndex update=new TagIndex();
		update.setKiiAppIDs(appIDs);
		update.setGlobalThings(thingIDs);

		super.updateEntityWithVersion(update,tagIdx.getId(),tagIdx.getVersion());

	}

	public void removeThingFromTag(TagIndex tagIdx,List<GlobalThingInfo> things){
		
		Set<String> removeThings=things.stream().map(GlobalThingInfo::getId).collect(Collectors.toSet());

		Set<String> removeApps=things.stream().map(GlobalThingInfo::getKiiAppID).collect(Collectors.toSet());


		Set<String> currThings=tagIdx.getGlobalThings();
		currThings.removeAll(removeThings);
		
		Set<String> curraAppIDs=tagIdx.getKiiAppIDs();
		curraAppIDs.removeAll(removeApps);

		TagIndex update=new TagIndex();
		update.setKiiAppIDs(curraAppIDs);
		update.setGlobalThings(currThings);

		super.updateEntityWithVersion(update,tagIdx.getId(),tagIdx.getVersion());

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
		return new BucketInfo(BUCKET_INFO);
	}



}
