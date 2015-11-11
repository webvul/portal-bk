package com.kii.beehive.portal.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
@Component
public class GlobalThingDao extends AbstractDataAccess<GlobalThingInfo>{



	public void bindTagsToThing(String[] tags,GlobalThingInfo thing){

		Set<String> currTags=thing.getTags();

		currTags.addAll(Arrays.asList(tags));

		Map<String,Object> valMap=new HashMap<>();
		valMap.put("tag",currTags);

		super.updateEntityWithVersion(valMap, thing.getId(), thing.getVersion());

	}

	public void unbindTagsToThing(String[] tags,GlobalThingInfo thing){

		Set<String> currTags=thing.getTags();

		currTags.removeAll(Arrays.asList(tags));

		Map<String,Object> valMap=new HashMap<>();
		valMap.put("tag",currTags);

		super.updateEntityWithVersion(valMap, thing.getId(), thing.getVersion());

	}

	public GlobalThingInfo getThingInfoByID(String id){
		return super.getObjectByID(id);
	}

	public List<GlobalThingInfo> getThingsByIDs(String[] ids){
		return super.getEntitys(ids);
	}

	public void addThingInfo(GlobalThingInfo thing){
		super.addKiiEntity(thing);
	}

	@Override
	protected Class<GlobalThingInfo> getTypeCls() {
		return GlobalThingInfo.class;
	}



	@Override
	protected BucketInfo getBucketInfo() {

		return new BucketInfo("GlobalThingInfo");
	}
}
