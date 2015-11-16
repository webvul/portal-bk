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
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
@Component
public class GlobalThingDao extends AbstractDataAccess<GlobalThingInfo>{

	private final String TAGS = "tags";
	private final String BUCKET_INFO = "GlobalThingInfo";

	public void bindTagsToThing(String[] tags,GlobalThingInfo thing){

		Set<String> currTags=thing.getTags();
		currTags.addAll(Arrays.asList(tags));

		Map<String,Object> valMap=new HashMap<>();
		valMap.put(TAGS,currTags);

		super.updateEntityWithVersion(valMap, thing.getId(), thing.getVersion());

	}

	public void unbindTagsToThing(String[] tags,GlobalThingInfo thing){

		Set<String> currTags=thing.getTags();
		currTags.removeAll(Arrays.asList(tags));
		Map<String,Object> valMap=new HashMap<>();
		valMap.put(TAGS,currTags);

		super.updateEntityWithVersion(valMap, thing.getId(), thing.getVersion());
	}

	public void removeGlobalThingByID(String id){
		super.removeEntity(id);
	}
	
	public GlobalThingInfo getThingInfoByID(String id){
		return super.getObjectByID(id);
	}

	public GlobalThingInfo getThingByVendorThingID(String vendorThingID) {

		return super.getEntity("vendorThingID", vendorThingID);
	}

	public List<GlobalThingInfo> getThingsByIDs(String[] ids){
		return super.getEntitys(ids);
	}

	public void addThingInfo(GlobalThingInfo thing){
		super.addKiiEntity(thing);
	}

	public List<GlobalThingInfo> getAllThing() {
		return super.query(ConditionBuilder.getAll().getFinalCondition().build());
	}
	
	public List<GlobalThingInfo> query(QueryParam queryParam) {
		return super.query(queryParam);
	}

	@Override
	protected Class<GlobalThingInfo> getTypeCls() {
		return GlobalThingInfo.class;
	}



	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo(BUCKET_INFO);
	}


}
