package com.kii.beehive.portal.web.controller;

import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.exception.PortalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hdchen on 3/24/16.
 */
public abstract class AbstractThingTagController extends AbstractController {
	@Autowired
	protected TagThingManager thingTagManager;

	protected List<GlobalThingInfo> getThings(String globalThingIDs) {
		try {
			return thingTagManager.getThings(Arrays.asList(globalThingIDs.split(",")));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested thing doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected List<BeehiveUser> getUsers(String userIDs) {
		try {
			return thingTagManager.getUsers(Arrays.asList(userIDs.split(",")));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested user doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected List<UserGroup> getUserGroups(String userGroupIDs) {
		try {
			return thingTagManager.getUserGroups(Arrays.asList(userGroupIDs.split(",")));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested user group doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected List<TagIndex> getTags(String tagIDs) {
		try {
			return thingTagManager.getTagIndexes(Arrays.asList(tagIDs.split(",")));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested tag doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
