package com.kii.beehive.portal.store.entity.thing;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.kii.extension.sdk.entity.thingif.CommandDetail;

/**
 * Created by USER on 6/21/16.
 */
public class Commands extends CommandDetail {

	@Override
	@JsonSetter("_id")
	public void setCommandID(String commandID) {
		super.setCommandID(commandID);
	}

	@Override
	@JsonSetter("_created")
	public void setCreatedAt(Date createdAt) {
		super.setCreatedAt(createdAt);
	}

	@Override
	@JsonSetter("_modified")
	public void setModifiedAt(Date modifiedAt) {
		super.setModifiedAt(modifiedAt);
	}
}
