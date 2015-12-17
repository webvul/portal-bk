package com.kii.beehive.portal.service;

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

import com.kii.beehive.portal.exception.StoreException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;

@Component
@Transactional
public class ThingTagService {
	private Logger log= LoggerFactory.getLogger(ThingTagService.class);

	@Autowired
	private GlobalThingDao globalThingDao;

	@Autowired
	private TagIndexDao tagIndexDao;
	
	@Autowired
	private TagThingRelationDao tagThingRelationDao;
	
	//@Autowired
	//private AppInfoDao appInfoDao;


	public Long createThing(GlobalThingInfo thingInfo, Collection<String> tagList) {
		
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
		tagThingRelationDao.delete(thing.getId(), null);
		globalThingDao.deleteByID(thing.getId());
	}

	public List<String> findLocations(String parentLocation) {

		return tagIndexDao.findLocations(parentLocation);

	}

	public GlobalThingInfo getThingByVendorThingID(String vendorThingID){

		return globalThingDao.getThingByVendorThingID(vendorThingID);
	}

	public List<GlobalThingInfo> getThingsByTag(String tagName){

		TagIndex tagIndex=new TagIndex(tagName);
		return globalThingDao.findThingByTag(tagIndex.getFullTagName());
	}


	public List<GlobalThingInfo> queryThingByTagExpress(boolean b, List<String> tagList) {
		if(b){
			return globalThingDao.queryThingByIntersectionTags(tagList);
		}else {
			return globalThingDao.queryThingByUnionTags(tagList);
		}
	}
}
