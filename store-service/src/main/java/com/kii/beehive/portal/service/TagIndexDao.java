package com.kii.beehive.portal.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.store.entity.TagType;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class TagIndexDao extends AbstractDataAccess<TagIndex> {

	private Logger log= LoggerFactory.getLogger(TagIndexDao.class);
	
	private final String BUCKET_INFO = "TagThingInfo";
	
	public List<TagIndex> getAllTag() {
		return super.query(ConditionBuilder.getAll().getFinalCondition().build());
	}
	
	public List<TagIndex> findTagIndexByTagNameArray(String[] tagNameArray){
		List<TagIndex> tagIndexList = super.getEntitys(tagNameArray);
		return tagIndexList;
	}
	
	public void removeTagByID(String id){
		super.removeEntity(id);
	}


	public void bindThingToTag(Collection<String> tagIDs,List<GlobalThingInfo> things){

		if(tagIDs.size()==1) {
			TagIndex tag = getTagIndexByID(tagIDs.iterator().next());

			addThingToTag(tag, things);

		}else{
			List<TagIndex> tags = getTagsByIDs(tagIDs);
			log.debug("tags: " + tags);
			for (TagIndex tag : tags) {
				addThingToTag(tag, things);
			}
		}
	}

	public void unbindThingFromTag(Collection<String> tagIDs,List<GlobalThingInfo> things){

		if(tagIDs.size()==1) {
			TagIndex tag = getTagIndexByID(tagIDs.iterator().next());

			removeThingFromTag(tag, things);

		}else{
			List<TagIndex> tags = getTagsByIDs(tagIDs);
			log.debug("tags: " + tags);
			for (TagIndex tag : tags) {
				removeThingFromTag(tag, things);
			}
		}
	}
	
	private void addThingToTag(TagIndex tagIdx,List<GlobalThingInfo> things){

		Set<String> newThings=things.stream().map(GlobalThingInfo::getId).collect(Collectors.toSet());

		Set<String> newApps=things.stream().map(GlobalThingInfo::getKiiAppID).collect(Collectors.toSet());

		Set<String> thingIDs=tagIdx.getThings();
		thingIDs.addAll(newThings);

		Set<String> appIDs=tagIdx.getKiiAppIDs();
		appIDs.addAll(newApps);

		TagIndex update=new TagIndex();
		update.setKiiAppIDs(appIDs);
		update.setThings(thingIDs);

		super.updateEntityWithVersion(update,tagIdx.getId(),tagIdx.getVersion());

	}

	private void removeThingFromTag(TagIndex tagIdx,List<GlobalThingInfo> things){
		
		Set<String> removeThings=things.stream().map(GlobalThingInfo::getId).collect(Collectors.toSet());

		Set<String> removeApps=things.stream().map(GlobalThingInfo::getKiiAppID).collect(Collectors.toSet());


		Set<String> currThings=tagIdx.getThings();
		currThings.removeAll(removeThings);
		
		Set<String> curraAppIDs=tagIdx.getKiiAppIDs();
		curraAppIDs.removeAll(removeApps);

		TagIndex update=new TagIndex();
		update.setKiiAppIDs(curraAppIDs);
		update.setThings(currThings);

		super.updateEntityWithVersion(update,tagIdx.getId(),tagIdx.getVersion());

	}

	public boolean isTagIndexExist(String id){
		return super.checkExist(id);
	}


	public TagIndex getTagIndexByID(String id){
		return super.getObjectByID(id);
	}

	public TagIndex getCustomTagIndexByID(String id){
		return super.getObjectByID(TagType.Custom.getTagName(id));
	}

	public String addTagIndex(TagIndex tag) {
		tag.fillID();
		return super.addKiiEntity(tag);
	}

	public List<TagIndex> getTagsByIDs(Collection<String> ids){
		return super.getEntitys(ids.toArray(new String[0]));
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
