package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.ArchiveBeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal")
@Component
public class ArchiveBeehiveUserDao extends AbstractDataAccess<ArchiveBeehiveUser> {

	public void archive(BeehiveUser user){

		ArchiveBeehiveUser archiveUser = convert(user);

		super.addEntity(archiveUser, archiveUser.getBeehiveUserID());

	}

	private ArchiveBeehiveUser convert(BeehiveUser user) {

		ArchiveBeehiveUser archiveUser = new ArchiveBeehiveUser();

		archiveUser.setBeehiveUserID(user.getBeehiveUserID());

		archiveUser.setKiiUserID(user.getKiiUserID());

		archiveUser.setParty3rdID(user.getParty3rdID());

		archiveUser.setUserName(user.getUserName());

		archiveUser.setPhone(user.getPhone());

		archiveUser.setMail(user.getMail());

		archiveUser.setRole(user.getRole());

		archiveUser.setCompany(user.getCompany());

		archiveUser.setGroups(user.getGroups());

		archiveUser.setCustomFields(user.getCustomFields());

		return archiveUser;
	}

	@Override
	protected Class<ArchiveBeehiveUser> getTypeCls() {
		return ArchiveBeehiveUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("archiveBeehiveUser");
	}

}
