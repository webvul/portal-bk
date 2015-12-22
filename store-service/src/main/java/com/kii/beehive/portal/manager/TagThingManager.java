package com.kii.beehive.portal.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.exception.StoreException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;

@Component
public class TagThingManager {
	private Logger log= LoggerFactory.getLogger(TagThingManager.class);

	@Autowired
	private GlobalThingDao globalThingDao;

	@Autowired
	private TagIndexDao tagIndexDao;
	
	@Autowired
	private TagThingRelationDao tagThingRelationDao;
	
	//@Autowired
	//private AppInfoDao appInfoDao;


	public Long createThing(GlobalThingInfo thingInfo, String location, Collection<String> tagList) {
		
		/*KiiAppInfo kiiAppInfo = appInfoDao.getAppInfoByID(thingInfo.getKiiAppID());
		
		if(kiiAppInfo == null){
			EntryNotFoundException ex= new EntryNotFoundException(thingInfo.getKiiAppID());
			ex.setMessage("AppID not exist");
			throw ex;
		}*/


		Set<TagIndex> tagSet=new HashSet<>();

		tagList.forEach((str)->{
			tagSet.add(TagIndex.generCustomTagIndex(str));
		});

		long thingID = globalThingDao.saveOrUpdate(thingInfo);

		// set location tag and location tag-thing relation
		if(location != null) {
			this.saveOrUpdateThingLocation(thingID, location);
		}

		for(TagIndex tag:tagSet){
			if(!Strings.isBlank(tag.getDisplayName())){
				Long tagID = null;
				List<TagIndex>  list = tagIndexDao.findTagByTagTypeAndName(tag.getTagType().toString(), tag.getDisplayName());
				if( list.size() == 0) {
					tagID = tagIndexDao.saveOrUpdate(tag);
				}else{
					tagID = list.get(0).getId();
				}
				
				tagThingRelationDao.saveOrUpdate(new TagThingRelation(tagID,thingID));
			}
		}

		return thingID;
	}
	
	public void bindTagToThing(Collection<String> tagIDs,Long thingID) {
		GlobalThingInfo thing = globalThingDao.findByID(thingID);
		if(thing == null){
			throw new StoreException("thing not found");
		}
		
		for(String tagID:tagIDs){
			TagIndex tag = tagIndexDao.findByID(tagID);
			if(tag != null){
				tagThingRelationDao.saveOrUpdate(new TagThingRelation(tag.getId(),thing.getId()));
			}else{
				log.warn("Tag is null, TagId = " + tagID);
			}
		}
	}
	
	public void unbindTagToThing(Collection<String> tagIDs,Long thingID) {
		GlobalThingInfo thing = globalThingDao.findByID(thingID);
		if(thing == null){
			throw new StoreException("thing not found");
		}
		
		for(String tagID:tagIDs){
			TagIndex tag = tagIndexDao.findByID(tagID);
			if(tag != null){
				tagThingRelationDao.delete(tag.getId(),thing.getId());
			}else{
				log.warn("Tag is null, TagId = " + tagID);
			}
		}
	}

	public void removeTag(TagIndex tag) {
		tagThingRelationDao.delete(null, tag.getId());
		tagIndexDao.deleteByID(tag.getId());
	}
	
	public void removeThing(GlobalThingInfo thing) {
		tagThingRelationDao.delete(null, thing.getId());
		globalThingDao.deleteByID(thing.getId());
	}

	public List<String> findLocations(String parentLocation) {

		return tagIndexDao.findLocations(parentLocation);

	}


	private void saveOrUpdateThingLocation(Long globalThingID, String location) {

		// get location tag
		TagIndex tagIndex = tagIndexDao.findOneTagByTagTypeAndName(TagType.Location, location);

		if(tagIndex == null) {
			tagIndex = TagIndex.generTagIndex(TagType.Location, location, null);
			long tagID = tagIndexDao.saveOrUpdate(tagIndex);
			tagIndex.setId(tagID);
		}

		// get tag-thing relation
		List<TagThingRelation> relationList = tagThingRelationDao.find(globalThingID, TagType.Location, null);

		TagThingRelation relation = CollectUtils.getFirst(relationList);
		if(relation == null) {
			relation = new TagThingRelation(tagIndex.getId(), globalThingID);
		} else {
			relation.setTagID(tagIndex.getId());
		}

		// update tag-thing relation
		tagThingRelationDao.saveOrUpdate(relation);
	}

	public GlobalThingInfo findThingByVendorThingID(String vendorThingID) {
		List<GlobalThingInfo> list = globalThingDao.findBySingleField(GlobalThingInfo.VANDOR_THING_ID, vendorThingID);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public void bindCustomTagToThing(Collection<String> displayNames,Long thingID) {
		GlobalThingInfo thing = globalThingDao.findByID(thingID);
		if(thing == null){
			throw new StoreException("thing not found");
		}

		for(String displayName:displayNames){
			TagIndex tag = this.findCustomTag(displayName);
			if(tag != null){
				tagThingRelationDao.saveOrUpdate(new TagThingRelation(tag.getId(),thing.getId()));
			}else{
				log.warn("Custom Tag is null, displayName = " + displayName);
			}
		}
	}

	public void unbindCustomTagToThing(Collection<String> displayNames,Long thingID) {
		GlobalThingInfo thing = globalThingDao.findByID(thingID);
		if(thing == null){
			throw new StoreException("thing not found");
		}

		for(String displayName:displayNames){
			TagIndex tag = this.findCustomTag(displayName);
			if(tag != null){
				tagThingRelationDao.delete(tag.getId(),thing.getId());
			}else{
				log.warn("Custom Tag is null, displayName = " + displayName);
			}
		}
	}

	private TagIndex findCustomTag(String displayName) {
		List<TagIndex> tagIndexList = tagIndexDao.findTagByTagTypeAndName(TagType.Custom.toString(), displayName);

		if(tagIndexList == null || tagIndexList.isEmpty()) {
			return null;
		}
		return tagIndexList.get(0);
	}

	public List<TagIndex> findTagIndexByGlobalThingID(String globalThingID) {

		return tagIndexDao.findTagByGlobalThingID(globalThingID);
	}


}
