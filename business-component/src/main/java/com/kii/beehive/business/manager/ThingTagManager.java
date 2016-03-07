package com.kii.beehive.business.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.PagerTag;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
@Transactional
public class ThingTagManager {


	@Autowired
	private TagIndexDao tagDao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private ObjectMapper mapper;

	public Set<String> getTagNamesByIDs(List<Long> tagIDs){


		return tagDao.findByIDs(tagIDs).stream().map(tag->tag.getFullTagName()).collect(Collectors.toSet());
	}

	public void updateKiicloudRelation(String vendorID,String fullKiiThingID){
		globalThingDao.updateKiiThingID(vendorID,fullKiiThingID);
	}

	public void updateState(ThingStatus status,String thingID, String appID){

		String fullThingID= ThingIDTools.joinFullKiiThingID(thingID,appID);

		try {
			String stateJson = mapper.writeValueAsString(status);
			globalThingDao.updateState(stateJson,fullThingID);

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}

	}



	public GlobalThingInfo getThingByID(long globalThingID) {

		return globalThingDao.findByID(globalThingID);
	}

	public List<GlobalThingInfo> getThingInfos(TagSelector source) {
		List<GlobalThingInfo> things = new ArrayList<>();

		if(!source.getThingList().isEmpty()) {
			things.addAll(globalThingDao.getThingsByIDArray(source.getThingList()));
			return things;
		}

		if(!source.getTagList().isEmpty()) {
			if(StringUtils.isEmpty(source.getType())) {

				if (source.isAndExpress()) {
					things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList()));
				} else {
					things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList()));
				}
			}else{

				if (source.isAndExpress()) {
					things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList(),source.getType()));
				} else {
					things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList(),source.getType()));
				}
			}
		}
		return things;
	}


	public Set<String> getKiiThingIDs(TagSelector source){

		List<GlobalThingInfo> thingList=getThingInfos(source);

		return thingList.stream().map(thing->thing.getFullKiiThingID()).collect(Collectors.toSet());

	}
	
	public void  iteratorAllThingsStatus(Consumer<GlobalThingInfo> consumer) {

		 PagerTag pager=new PagerTag();
		 pager.setPageSize(50);
		 pager.setStartRow(0);

		 List<GlobalThingInfo> list= globalThingDao.getAllThing(pager);


		 while(pager.hasNext()){

			 list.forEach(consumer);
			 list=globalThingDao.getAllThing(pager);
		 }

		 list.forEach(consumer);

	}
}