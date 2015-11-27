package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;

@Component
public class ThingManager {

	private Logger log= LoggerFactory.getLogger(ThingManager.class);

	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private GlobalThingDao globalThingDao;


	@Autowired
	private TagIndexDao tagIndexDao;

	public String createThing(GlobalThingInfo thingInfo, Collection<String> tagList) {

		Set<TagIndex> tagSet=new HashSet<>();

		tagList.forEach((str)->{
			tagSet.add(TagIndex.generCustomTagIndex(str));
		});
		thingInfo.setGlobalThingID(thingInfo.getVendorThingID());
		return createThingWithTag(thingInfo,tagSet);
	}


	private String createThingWithTag(GlobalThingInfo thingInfo, Collection<TagIndex> tagList){

//		thingInfo.generGlobalThingID();

		// do not throw application exception for system-error (data not complete)

		KiiAppInfo masterAppInfo = appInfoDao.getMasterAppInfo();
		String defaultThingOwnerID = masterAppInfo.getDefaultThingOwnerID();

		thingInfo.setDefaultOwnerID(defaultThingOwnerID);

		thingInfo.setStatusUpdatetime(new Date());
		globalThingDao.addThingInfo(thingInfo);

		Set<String> tagNameSet = new HashSet<String>();
			for(TagIndex tag:tagList){
				if(!StringUtils.isEmpty(tag.getDisplayName()) && !StringUtils.isEmpty(tag.getTagType())){
					if(!tagIndexDao.isTagIndexExist(tag.getId())) {
						tagNameSet.add(tag.getId());
					}
				}
			}
		this.bindTagToThing(tagNameSet, Collections.singleton(thingInfo.getId()));

		return thingInfo.getGlobalThingID();
	}

	
	public void bindTagToThing(String tagID,String thingID) {
		this.bindTagToThing(Collections.singleton(tagID), Collections.singleton(thingID));
	}


	public void bindTagToThing(Collection<String> tagIDs,Collection<String> thingIDs) {

		if(tagIDs.isEmpty()||thingIDs.isEmpty()){
			return;
		}
		// save to globalThing
		List<GlobalThingInfo> things=new ArrayList<>();
		if (thingIDs.size() == 1) {

			GlobalThingInfo thing=globalThingDao.getThingInfoByID(thingIDs.iterator().next());

			globalThingDao.bindTagsToThing(tagIDs, thing);
			things.add(thing);

		} else{
			things = globalThingDao.getThingsByIDs(thingIDs);

			for (GlobalThingInfo thing : things) {
				globalThingDao.bindTagsToThing(tagIDs, thing);
			}
		}

		tagIndexDao.bindThingToTag(tagIDs,things);
		// save to tagIndex

	}
	
	public void unbindTagToThing(String tagID,String thingID) {
		GlobalThingInfo thing=globalThingDao.getThingInfoByID(thingID);
		globalThingDao.removeTagsFromThing(thing, new String[]{tagID});
		
//		TagIndex tag = tagIndexDao.getTagIndexByID(tagID);
		tagIndexDao.unbindThingFromTag(Collections.singletonList(tagID), Arrays.asList(thing));
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
		Collection<String> tagSet = orig.getTags();
//		for(String tagID:tagSet){
//			TagIndex tag = tagIndexDao.getTagIndexByID(tagID);
		tagIndexDao.unbindThingFromTag(tagSet, Arrays.asList(orig));
//		}
		
		globalThingDao.removeGlobalThingByID(orig.getId());
	}
	
	public List<GlobalThingInfo> findThingByTagName(String[] tagArray, String operation){
//		List<GlobalThingInfo> list = null;
		List<TagIndex> tagList = tagIndexDao.findTagIndexByTagNameArray(tagArray);
		if(tagList.size()==0){
			return new ArrayList<>();
		}

		Set<String> thingsSet = new HashSet<String>();
		thingsSet.addAll(tagList.iterator().next().getGlobalThings());
		for(TagIndex ti:tagList){
			if(operation.equals("or")) {
				thingsSet.addAll(ti.getGlobalThings());
			}else if(operation.equals("and")){
				thingsSet.retainAll(ti.getGlobalThings());
			}
		}

		return  globalThingDao.getThingsByIDs(thingsSet);

		
//		if(operation.equals("and")){
//			Set<String> tagSet = new HashSet<String>(Arrays.asList(tagArray));
//			for(int i=0; i< list.size() ;i++){
//				if(!list.get(i).getTags().containsAll(tagSet)){//include part of
//					list.remove(i);
//				}
//			}
//		}
//		return list;
	}

	public  GlobalThingInfo findThingByVendorThingID(String vendorThingID) {
		GlobalThingInfo info= globalThingDao.getThingByVendorThingID(vendorThingID);

		if(info == null) {
			EntryNotFoundException ex= new EntryNotFoundException(vendorThingID);
			ex.setMessage(" vendor thingID not exist ");
			throw ex;

		}
		return info;
	}


}
