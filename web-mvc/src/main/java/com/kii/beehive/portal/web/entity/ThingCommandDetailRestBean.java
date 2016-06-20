package com.kii.beehive.portal.web.entity;

import org.springframework.beans.BeanUtils;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.entity.thingif.CommandDetail;

/**
 * Created by USER on 6/19/16.
 */
public class ThingCommandDetailRestBean extends CommandDetail {

	private long globalThingID = 0;

	public ThingCommandDetailRestBean(GlobalThingInfo thing, CommandDetail commandDetail) {

		this.globalThingID = thing.getId();

		BeanUtils.copyProperties(commandDetail, this);
	}

	public long getGlobalThingID() {
		return globalThingID;
	}

}
