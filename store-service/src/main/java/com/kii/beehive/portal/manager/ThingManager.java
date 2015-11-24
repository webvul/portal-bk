package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kii.extension.sdk.exception.ObjectNotFoundException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.helper.AppInfoService;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.exception.KiiCloudException;

@Component
public class ThingManager {

	@Autowired
	private GlobalThingDao globalThingDao;

	@Autowired
	private AppInfoService appInfoDao;

	@Autowired
	private TagIndexDao tagIndexDao;


	public void createThing(GlobalThingInfo thingInfo, List<TagIndex> tagList){

		String globalThingID = thingInfo.getGlobalThingID();
		if(Strings.isBlank(globalThingID)) {
			globalThingID = this.generateGlobalThingID(thingInfo);
			thingInfo.setGlobalThingID(globalThingID);
		}

		if(thingInfo.getKiiAppID()==null){
			AppInfo appInfo=appInfoDao.getMatchAppInfoByThing(thingInfo.getVendorThingID());
			thingInfo.setKiiAppID(appInfo.getAppID());
		}

		// get default thing owner id by Kii App ID
		// throw exception if default thing owner not found
		String defaultThingOwnerID = appInfoDao.getDefaultThingOwnerID(thingInfo.getKiiAppID());
		if(Strings.isBlank(defaultThingOwnerID)) {
			throw new ObjectNotFoundException();
		}

		thingInfo.setDefaultOwnerID(defaultThingOwnerID);
		thingInfo.setStatusUpdatetime(new Date());
		globalThingDao.addThingInfo(thingInfo);
		Set<String> tagNameSet = new HashSet<String>();
		if(tagList != null && tagList.size() > 0){
			for(TagIndex tag:tagList){
				if(!Strings.isBlank(tag.getDisplayName()) && !Strings.isBlank(tag.getTagType())){
					this.checkTagAndCreate(tag);
					tagNameSet.add(tag.getId());
				}
			}
			this.bindTagToThing(tagNameSet.toArray(new String[tagNameSet.size()]), new String[]{thingInfo.getId()});
			
		}
	}
	
	public void checkTagAndCreate(TagIndex tag){
		try{
			tagIndexDao.getTagIndexByID(tag.getId());
		}catch(KiiCloudException e){// create tag
			tagIndexDao.addTagIndex(tag);
		}
	}
	
	public void bindTagToThing(String tagID,String thingID) {
		this.bindTagToThing(new String[]{tagID}, new String[]{thingID});
	}


	public void bindTagToThing(String[] tagIDs,String[] thingIDs) {

		// save to globalThing
		List<GlobalThingInfo> things=new ArrayList<>();
		if (thingIDs.length == 1) {

			GlobalThingInfo thing=globalThingDao.getThingInfoByID(thingIDs[0]);

			globalThingDao.bindTagsToThing(tagIDs, thing);
			things.add(thing);

		} else{
			things = globalThingDao.getThingsByIDs(thingIDs);

			for (GlobalThingInfo thing : things) {
				globalThingDao.bindTagsToThing(tagIDs, thing);
			}
		}
		
		// save to tagIndex
		if(tagIDs.length==1) {
			TagIndex tag = tagIndexDao.getTagIndexByID(tagIDs[0]);

			tagIndexDao.addThingToTag(tag, things);

		}else{
			List<TagIndex> tags = tagIndexDao.getTagsByIDs(tagIDs);

			for (TagIndex tag : tags) {
				tagIndexDao.addThingToTag(tag, things);
			}
		}
	}
	
	public void unbindTagToThing(String tagID,String thingID) {
		GlobalThingInfo thing=globalThingDao.getThingInfoByID(thingID);
		globalThingDao.removeTagsFromThing(thing, new String[]{tagID});
		
		TagIndex tag = tagIndexDao.getTagIndexByID(tagID);
		tagIndexDao.removeThingFromTag(tag, Arrays.asList(thing));
	}
	
	
	public void removeTag(TagIndex orig) {
		Set<String> thingIDSet = orig.getGlobalThings();
		for(String thingID:thingIDSet){
			GlobalThingInfo thing=globalThingDao.getThingInfoByID(thingID);
			globalThingDao.removeTagsFromThing(thing, new String[]{orig.getId()});
		}
		tagIndexDao.removeTagByID(orig.getId());
		
		
	}
	
	public void removeThings(GlobalThingInfo orig) {
		Set<String> tagSet = orig.getTags();
		for(String tagID:tagSet){
			TagIndex tag = tagIndexDao.getTagIndexByID(tagID);
			tagIndexDao.removeThingFromTag(tag, Arrays.asList(orig));
		}
		
		globalThingDao.removeGlobalThingByID(orig.getId());
	}
	
	public List<GlobalThingInfo> findThingByTagName(String[] tagArray, String operation){
		List<GlobalThingInfo> list = null;
		List<TagIndex> tagList = tagIndexDao.findTagIndexByTagNameArray(tagArray);
		
		Set<String> thingsSet = new HashSet<String>();
		for(TagIndex ti:tagList){
			thingsSet.addAll(ti.getGlobalThings());
		}
		
		if(thingsSet.size() > 0){
			list = globalThingDao.getThingsByIDs(thingsSet.toArray(new String[thingsSet.size()]));
		}
		
		if(operation.equals("and")){
			Set<String> tagSet = new HashSet<String>(Arrays.asList(tagArray));
			for(int i=0; i< list.size() ;i++){
				if(!list.get(i).getTags().containsAll(tagSet)){//include part of 
					list.remove(i);
				}
			}
		}
		return list;
	}

	public  GlobalThingInfo findThingByVendorThingID(String vendorThingID) {
		return globalThingDao.getThingByVendorThingID(vendorThingID);
	}

	private String generateGlobalThingID(GlobalThingInfo thingInfo) {

		String globalThingID = thingInfo.getKiiAppID() + "-" + thingInfo.getVendorThingID();

		return globalThingID;
	}

}
