package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.extension.sdk.entity.AppInfo;

@Component
public class ThingManager {

	@Autowired
	private GlobalThingDao globalThingDao;

	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private TagIndexDao tagIndexDao;


	public void createThing(GlobalThingInfo thingInfo){

		if(thingInfo.getAppID()==null){
			AppInfo appInfo=appInfoDao.getMatchAppInfoByThing(thingInfo.getVendorThingID());
			thingInfo.setAppID(appInfo.getAppID());
		}


	}

	public void bindTagToThing(String tagID,String thingID) {



		GlobalThingInfo thing=globalThingDao.getObjectByID(thingID);

		globalThingDao.bindTagsToThing(new String[]{tagID}, thing);

		TagIndex tag = tagIndexDao.getObjectByID(tagID);

		tagIndexDao.addThingToTag(tag, Arrays.asList(thing));
	}


	public void bindTagToThing(String[] tagIDs,String[] thingIDs) {

		// save to globalThing
		List<GlobalThingInfo> things=new ArrayList<>();
		if (thingIDs.length == 1) {

			GlobalThingInfo thing=globalThingDao.getObjectByID(thingIDs[0]);

			globalThingDao.bindTagsToThing(tagIDs, thing);
			things.add(thing);

		} else{
			things = globalThingDao.getEntitys(thingIDs);

			for (GlobalThingInfo thing : things) {
				globalThingDao.bindTagsToThing(tagIDs, thing);
			}
		}
		
		// save to tagIndex
		if(tagIDs.length==1) {
			TagIndex tag = tagIndexDao.getObjectByID(tagIDs[0]);

			tagIndexDao.addThingToTag(tag, things);

		}else{
			List<TagIndex> tags = tagIndexDao.getEntitys(tagIDs);

			for (TagIndex tag : tags) {
				tagIndexDao.addThingToTag(tag, things);
			}
		}

	}

}
