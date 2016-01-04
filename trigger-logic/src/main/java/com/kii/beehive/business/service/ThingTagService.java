package com.kii.beehive.business.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.beehive.portal.store.entity.trigger.TriggerTarget;

@Transactional
@Component
public class ThingTagService {

	@Autowired
	private GlobalThingDao globalThingDao;

	public List<GlobalThingInfo> queryThingByTagExpress(boolean isAnd, List<String> tagCollect) {

		if(isAnd){
			return globalThingDao.queryThingByIntersectionTags(tagCollect);
		}else{
			return globalThingDao.queryThingByUnionTags(tagCollect);
		}


	}

	public GlobalThingInfo getThingByID(long globalThingID) {

		return globalThingDao.getThingByID(globalThingID);
	}

	public List<GlobalThingInfo> getThingInfos(TriggerSource source) {
		List<GlobalThingInfo> things = new ArrayList<>();

		things.addAll(globalThingDao.getThingsByIDArray(source.getThingList()));

		if(source.isAndExpress()) {
			things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList()));
		}else{
			things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList()));
		}
		return things;
	}

	public List<GlobalThingInfo> getThingInfos(TriggerTarget target) {
		List<GlobalThingInfo> things = new ArrayList<>();

		things.addAll(globalThingDao.getThingsByIDArray(target.getThingList()));

		if(target.isAndExpress()) {
			things.addAll(globalThingDao.queryThingByIntersectionTags(target.getTagList()));
		}else{
			things.addAll(globalThingDao.queryThingByUnionTags(target.getTagList()));
		}
		return things;
	}


}
