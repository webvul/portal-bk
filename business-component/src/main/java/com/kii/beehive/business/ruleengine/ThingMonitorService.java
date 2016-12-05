package com.kii.beehive.business.ruleengine;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.ThingStatusMonitorDao;
import com.kii.beehive.portal.service.UserNotificationDao;
import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.beehive.portal.store.entity.UserNotification;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component

public class ThingMonitorService {

	@Autowired
	private UserNotificationDao notificationDao;

	@Autowired
	private ThingStatusMonitorDao  monitorDao;

	@Autowired
	private GlobalThingSpringDao thingDao;


	@Transactional
	public void addNotifiction(String thingID,ThingStatus status, String userID){


		UserNotification  notice=new UserNotification();
		notice.setFrom("Sys");
		notice.setMessage(status);
		notice.setReaded(false);

		ThingIDTools.ThingIDCombine ids=ThingIDTools.splitFullKiiThingID(thingID);

		GlobalThingInfo th=thingDao.getThingByFullKiiThingID(ids.kiiAppID,ids.kiiThingID);

		notice.setTitle("thing "+th.getVendorThingID()+"'s status change ");
		notice.setType(UserNotification.MsgType.ThingStatus);
	}

	public void addMonitor(ThingStatusMonitor  monitor){




	}



}
