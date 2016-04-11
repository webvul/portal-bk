package com.kii.beehive.portal.web.controller;

import static com.kii.beehive.portal.common.utils.CollectUtils.collectionToString;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Created by hdchen on 3/24/16.
 */
public abstract class AbstractThingTagController extends AbstractController {
	@Autowired
	protected TagThingManager thingTagManager;

	protected List<GlobalThingInfo> getThings(List<String> thingIDList) {
		try {
			return thingTagManager.getThingsByIdStrings(thingIDList);
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested thing doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected List<GlobalThingInfo> getCreatedThings(String globalThingIds) {
		List<Long> thingIds = getCreatedThingIds(globalThingIds);
		try {
			return thingTagManager.getThingsByIds(thingIds);
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested thing doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected List<Long> getCreatedThingIds(String globalThingIds) {
		Set<String> strThingIds = Arrays.asList(globalThingIds.split(",")).stream().collect(Collectors.toSet());
		List<Long> thingIds = strThingIds.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).
				map(Long::valueOf).collect(Collectors.toList());
		if (strThingIds.size() != thingIds.size()) {
			thingIds.forEach(id -> strThingIds.remove(id.toString()));
			throw new PortalException("Invalid thing id(s)",
					"Invalid thing id(s): [" + collectionToString(strThingIds) + "]", HttpStatus.BAD_REQUEST);
		}
		try {
			return thingTagManager.getCreatedThingIds(AuthInfoStore.getUserID(), thingIds);
		} catch (ObjectNotFoundException e) {
			throw new BeehiveUnAuthorizedException("Requested thing doesn't exist or isn't created by user " +
					AuthInfoStore.getUserID());
		}
	}

	/**
	 * @param fullTagNames a list of tag full names, separating by comma
	 * @return a list of tag ids
	 * @throws PortalException if no tags can be found
	 */
	protected List<Long> getCreatedTagIds(String fullTagNames) {
		try {
			return thingTagManager.getCreatedTagIdsByFullTagName(AuthInfoStore.getUserID(), fullTagNames);
		} catch (ObjectNotFoundException e) {
			throw new PortalException(e.getMessage(), e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected List<Long> getCreatedTagIds(TagType type, String displayNames) {
		try {
			return thingTagManager.getCreatedTagIdsByTypeAndDisplayNames(AuthInfoStore.getUserID(), type,
					Arrays.asList(displayNames.split(",")));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested tag doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected Set<String> getUserIds(String userIDs) {
		try {
			return thingTagManager.getUsers(Arrays.asList(userIDs.split(","))).stream().
					map(BeehiveUser::getKiiLoginName).collect(Collectors.toSet());
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested user doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected List<Long> getUserGroupIds(String userGroupIDs) {
		try {
			return thingTagManager.getUserGroupIds(Arrays.asList(userGroupIDs.split(",")));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested user group doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected List<TagIndex> getTags(List<String> fullNameList) {
		try {
			return thingTagManager.getTagFullNameIndexes(fullNameList);
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested tag doesn't exist", e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException("Current user is not the creator of tag(s).");
		}
	}
}
