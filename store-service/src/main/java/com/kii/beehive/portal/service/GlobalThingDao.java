package com.kii.beehive.portal.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.helper.SimpleQueryTool;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
@Component
public class GlobalThingDao extends AbstractDataAccess<GlobalThingInfo>{


	private Logger log= LoggerFactory.getLogger(GlobalThingDao.class);


	@Autowired
	private SimpleQueryTool queryTool;

	private final String TAGS = "tags";
	private final String BUCKET_INFO = "GlobalThingInfo";

	public void bindTagsToThing(Collection<String> tags, GlobalThingInfo thing){

		Set<String> currTags=thing.getTags();
		currTags.addAll(tags);

		Map<String,Object> valMap=new HashMap<>();
		valMap.put(TAGS,currTags);

		super.updateEntityWithVersion(valMap, thing.getId(), thing.getVersion());

	}

	public void removeTagsFromThing(GlobalThingInfo thing, String[] tags){

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

		QueryParam query=queryTool.getSimpleQuery("vendorThingID",vendorThingID);

		List<GlobalThingInfo> list=super.query(query);

		if(list.size()>1){
			log.warn("duplicate vendor thing id");
		}else if(list.isEmpty()){
			return null;
		}

		return list.get(0);


	}

	public List<GlobalThingInfo> getThingsByIDs(Collection<String> ids){
		return super.getEntitys(ids.toArray(new String[0]));
	}

	public void addThingInfo(GlobalThingInfo thing){
		thing.setGlobalThingID(thing.getVendorThingID());

		super.updateEntityAll(thing);
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
