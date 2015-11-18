package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal")
@Component
public class ArchiveBeehiveUserDao extends AbstractDataAccess<BeehiveUser> {

	public void archive(BeehiveUser user){

		super.addEntity(user, user.getId());

	}

	@Override
	protected Class<BeehiveUser> getTypeCls() {
		return BeehiveUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("archiveBeehiveUser");
	}

}
