package com.kii.beehive.portal.manager;


import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.DuplicateException;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TeamThingRelation;
import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Transactional
public class ThingManager {

	@Autowired
	private GlobalThingSpringDao thingDao;


	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private TagThingRelationDao tagThingRelationDao;


	@Autowired
	private ThingUserRelationDao thingUserRelationDao;

	@Autowired
	private TeamThingRelationDao teamThingRelationDao;


	@Autowired
	private ThingLocationRelDao locationRelDao;


	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private ThingGeoDao thingGeoDao;

	public List<Map<String, Object>> getThingDetailByIDList(List<Long> thingIDs) {


		if (thingIDs == null || thingIDs.isEmpty()) {
			return new ArrayList<>();
		}

		return thingDao.getFullThingDetailByThingIDs(thingIDs);

	}


	/**
	 * create or update the thing including the location and custom tags
	 *
	 * @param thingInfo
	 * @param location
	 * @return
	 */
	public Long createEndNode(GlobalThingInfo thingInfo, String location, String gatewayID)
			throws ObjectNotFoundException, UnauthorizedException {

		GlobalThingInfo gateway = getThingsByVendorThingId(gatewayID);

		thingInfo.setCreateBy(gateway.getCreateBy());
		return createThing(thingInfo, location);
	}


	public Long createThing(GlobalThingInfo thingInfo, String location)
			throws ObjectNotFoundException, UnauthorizedException {
		Long id = thingInfo.getId();

		GlobalThingInfo existTh = getThingsByVendorThingId(thingInfo.getVendorThingID());

		if (existTh != null && !existTh.getId().equals(thingInfo.getId())) {
			throw new DuplicateException(thingInfo.getVendorThingID(), "thing");
		}

		KiiAppInfo kiiAppInfo = appInfoDao.getAppInfoByID(thingInfo.getKiiAppID());

		// check whether Kii App ID is existing
		if (kiiAppInfo == null) {
			throw EntryNotFoundException.appNotFound(thingInfo.getKiiAppID());
		}

		// check whether Kii App ID is Master App
		if (kiiAppInfo.getMasterApp()) {
			throw EntryNotFoundException.thingNotFound(thingInfo.getId());
		}


		if (id == null) {
			id = globalThingDao.insert(thingInfo);

			ThingUserRelation relation = new ThingUserRelation();
			relation.setBeehiveUserID(Long.valueOf(thingInfo.getCreateBy()));
			relation.setThingId(id);
			thingUserRelationDao.saveOrUpdate(relation);
			if (StringUtils.isNotBlank(location)) {
				locationRelDao.addRelation(id, Collections.singletonList(location));
			}

		} else {
			getCanUpdateThingById(AuthInfoStore.getUserID(), id);
			thingInfo.fillFullKiiThingID();
			globalThingDao.updateEntityByID(thingInfo, id);
		}


		if (null != AuthInfoStore.getTeamID()) {
			teamThingRelationDao.saveOrUpdate(new TeamThingRelation(AuthInfoStore.getTeamID(), id));
		}

		// sync globalThingID into thing_geo
		thingGeoDao.updateGlobalThingIDByVendorThingID(thingInfo.getVendorThingID(), id);

		return id;
	}


	private GlobalThingInfo getCanUpdateThingById(Long userId, Long thingId) throws ObjectNotFoundException {
		if (null != thingUserRelationDao.find(thingId, userId)) { // must be creator
			GlobalThingInfo thingInfo = globalThingDao.findByID(thingId);
			if (null != thingInfo) {
				return thingInfo;
			}
		}

		throw new UnauthorizedException(UnauthorizedException.NOT_THING_CREATOR, "user", String.valueOf(userId));

	}

	public GlobalThingInfo getThingsByVendorThingId(String vendorThingId) {
		return globalThingDao.getThingByVendorThingID(vendorThingId);
	}
}
