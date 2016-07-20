package com.kii.beehive.portal.manager;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;

@Component
@Transactional
public class LocationManager {



	@Autowired
	private ThingLocationRelDao relDao;

	@Autowired
	private ThingLocationDao thingLocDao;




	public void addRelation(Long thingID,List<String> locList){

		relDao.addRelation(thingID,locList);

	}


	public void removeRelation(Long thingID,List<String> locList){

		relDao.removeRelation(thingID,locList);

	}


	public void updateRelation(Long thingID,List<String> locList){

		relDao.clearAllRelation(thingID);

		relDao.addRelation(thingID,locList);

	}

	public void clearRelation(Long thingID){


		relDao.clearAllRelation(thingID);

	}
}
