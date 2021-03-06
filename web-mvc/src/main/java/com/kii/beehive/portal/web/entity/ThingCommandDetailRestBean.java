package com.kii.beehive.portal.web.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

/**
 * Created by USER on 6/19/16.
 */
public class ThingCommandDetailRestBean  {

	private long globalThingID = 0;

	private ThingCommand  command;

	private Date createdAt;

	private Date modifiedAt;

	public ThingCommandDetailRestBean(GlobalThingInfo thing, ThingCommand commandDetail) {

		this.globalThingID = thing.getId();

		this.command=commandDetail;

		this.createdAt = commandDetail.getCreated();
		this.modifiedAt = commandDetail.getModified();
	}

	@JsonUnwrapped
	public ThingCommand getCommand() {
		return command;
	}

	public void setCommand(ThingCommand command) {
		this.command = command;
	}

	public long getGlobalThingID() {
		return globalThingID;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

}
