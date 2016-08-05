package com.kii.beehive.portal.manager;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;

@Component
@Transactional
public class ThingManager {

	@Autowired
	private GlobalThingSpringDao  thingDao;

	public List<Map<String,Object>> getThingDetailByIDList(List<Long> thingIDs){


		if(thingIDs==null||thingIDs.isEmpty()){
			return new ArrayList<>();
		}

		return thingDao.getFullThingDetailByThingIDs(thingIDs);

	}
}
