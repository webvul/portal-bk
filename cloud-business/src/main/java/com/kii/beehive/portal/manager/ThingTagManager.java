package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexSpringDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
@Transactional
public class ThingTagManager {


	@Autowired
	private TagIndexSpringDao tagDao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private ObjectMapper mapper;

	public List<String> getTagNamesByIDs(List<Long> tagIDs){

//		long[] ids=  tagIDs.stream().mapToLong(Long::parseLong).toArray();

		return tagDao.findByIDs(tagIDs).stream().map(tag->tag.getFullTagName()).collect(Collectors.toList());
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
			if (source.isAndExpress() ) {
				things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList()));
			} else {
				things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList()));
			}
		}
		return things;
	}

}
