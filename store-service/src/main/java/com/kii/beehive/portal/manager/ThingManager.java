package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagThingIndex;

@Component
public class ThingManager {

	@Autowired
	private GlobalThingDao globalThingDao;


	@Autowired
	private TagIndexDao tagIndexDao;

	public void bindTagToThing(String tagID,String thingID) {

		GlobalThingInfo thing=globalThingDao.getObjectByID(thingID);

		globalThingDao.bindTagsToThing(new String[]{tagID}, thing);

		TagThingIndex tag = tagIndexDao.getObjectByID(tagID);

		tagIndexDao.addThingToTag(tag, Arrays.asList(thing));
	}


	public void bindTagToThing(String[] tagIDs,String[] thingIDs) {


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

		if(tagIDs.length==1) {
			TagThingIndex tag = tagIndexDao.getObjectByID(tagIDs[0]);

			tagIndexDao.addThingToTag(tag, things);

		}else{
			List<TagThingIndex> tags = tagIndexDao.getEntitys(tagIDs);

			for (TagThingIndex tag : tags) {
				tagIndexDao.addThingToTag(tag, things);
			}
		}

	}

}
