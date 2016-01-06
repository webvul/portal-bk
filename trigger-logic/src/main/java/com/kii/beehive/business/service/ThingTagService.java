package com.kii.beehive.business.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;

@Transactional
@Component
public class ThingTagService {

	@Autowired
	private GlobalThingDao globalThingDao;

//	public List<GlobalThingInfo> queryThingByTagExpress(boolean isAnd, List<String> tagCollect) {
//
//		if(isAnd){
//			return globalThingDao.queryThingByIntersectionTags(tagCollect);
//		}else{
//			return globalThingDao.queryThingByUnionTags(tagCollect);
//		}
//
//
//	}

	public GlobalThingInfo getThingByID(long globalThingID) {

		return globalThingDao.getThingByID(globalThingID);
	}

	public List<GlobalThingInfo> getThingInfos(TagSelector source) {
		List<GlobalThingInfo> things = new ArrayList<>();

		if(!source.getThingList().isEmpty()) {
			things.addAll(globalThingDao.getThingsByIDArray(source.getThingList()));
			return things;
		}

		if(source.isAndExpress()) {
			things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList()));
		}else{
			things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList()));
		}
		return things;
	}




}
