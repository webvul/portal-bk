package com.kii.beehive.portal.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;
import com.kii.beehive.portal.jdbc.dao.ThingUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;


@Component
@Transactional
public class GatewayCallinManager {



	@Autowired
	private GlobalThingSpringDao thingDao;


	@Autowired
	private ThingLocationRelDao locationRelDao;

	@Autowired
	private ThingUserRelationDao thingUserRelationDao;


	@Autowired
	private AppInfoDao appInfoDao;


	public Long createEndNode(String vendorThingID, List<String> location, String gatewayID)
			throws ObjectNotFoundException, UnauthorizedException {


		GlobalThingInfo  thingInfo=new GlobalThingInfo();
		thingInfo.setVendorThingID(vendorThingID);

		GlobalThingInfo gateway = thingDao.getThingByVendorThingID(gatewayID);

		thingInfo.setCreateBy(gateway.getCreateBy());
		thingInfo.setKiiAppID("DEMO");
		Long id=thingDao.insert(thingInfo);

		ThingUserRelation relation = new ThingUserRelation();
		relation.setBeehiveUserID(Long.valueOf(thingInfo.getCreateBy()));
		relation.setThingId(id);
		thingUserRelationDao.saveOrUpdate(relation);


		if(!location.isEmpty()) {
			locationRelDao.addRelation(id, location);
		}

		return id;
	}

	public Long updateThing(GlobalThingInfo thingInfo)
			throws ObjectNotFoundException, UnauthorizedException {


		GlobalThingInfo existTh =thingDao.getThingByVendorThingID(thingInfo.getVendorThingID());


		KiiAppInfo kiiAppInfo = appInfoDao.getAppInfoByID(thingInfo.getKiiAppID());

		// check whether Kii App ID is existing
		if (kiiAppInfo == null) {
			throw EntryNotFoundException.appNotFound(thingInfo.getKiiAppID());
		}

		// check whether Kii App ID is Master App
		if (kiiAppInfo.getMasterApp()) {
			throw EntryNotFoundException.thingNotFound(thingInfo.getId());
		}

		thingInfo.fillFullKiiThingID();
		thingDao.updateEntityByID(thingInfo,existTh.getId());

		return existTh.getId();
	}


}
