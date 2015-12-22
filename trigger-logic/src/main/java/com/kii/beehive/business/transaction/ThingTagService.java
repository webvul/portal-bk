package com.kii.beehive.business.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

@Transactional
@Service
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

	public GlobalThingInfo getThingByVendorThingID(String globalThingID) {

		return globalThingDao.getThingByVendorThingID(globalThingID);
	}
}
