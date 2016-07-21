package com.kii.beehive.portal.web.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Created by hdchen on 3/24/16.
 */
public abstract class AbstractThingTagController extends AbstractController {
	@Autowired
	protected TagThingManager thingTagManager;

	protected List<GlobalThingInfo> getThings(List<String> thingIDList) {
			return thingTagManager.getThingsByIdStrings(thingIDList);

	}

	protected List<GlobalThingInfo> getCreatedThings(String globalThingIds) {
		List<Long> thingIds = getCreatedThingIds(globalThingIds);
			return thingTagManager.getThingsByIds(thingIds);

	}

	protected List<Long> getCreatedThingIds(String globalThingIds) {
		Set<String> strThingIds = Arrays.asList(globalThingIds.split(",")).stream().collect(Collectors.toSet());
		List<Long> thingIds = strThingIds.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).
				map(Long::valueOf).collect(Collectors.toList());
		if (strThingIds.size() != thingIds.size()) {
			thingIds.forEach(id -> strThingIds.remove(id.toString()));
			throw new PortalException(ErrorCode.INVALID_INPUT,"field","thing","data",String.valueOf(thingIds));
		}
		return thingTagManager.getCreatedThingIds(AuthInfoStore.getUserIDInLong(), thingIds);

	}

	/**
	 * @param fullTagNames a list of tag full names, separating by comma
	 * @return a list of tag ids
	 * @throws PortalException if no tags can be found
	 */
	protected List<Long> getCreatedTagIds(String fullTagNames) {
		return thingTagManager.getCreatedTagIdsByFullTagName(AuthInfoStore.getUserIDInLong(), fullTagNames);

	}

	protected List<Long> getCreatedTagIds(TagType type, String displayNames) {
		return thingTagManager.getCreatedTagIdsByTypeAndDisplayNames(AuthInfoStore.getUserIDInLong(), type,
					Arrays.asList(displayNames.split(",")));

	}

	protected Set<String> getUserIds(String userIDs) {


		return new HashSet(Arrays.asList(userIDs.split(",")));
	}

	protected Set<Long> getUserGroupIds(String userGroupIDs) {

		Set<Long> idSet = Arrays.asList(userGroupIDs.split(",")).stream()
				.map(Long::valueOf).collect(Collectors.toSet());

		thingTagManager.checkUserGroupIds(idSet);

		return idSet;
	}

	protected List<TagIndex> getTags(List<String> fullNameList) {
		return thingTagManager.getTagFullNameIndexes(fullNameList);

	}
}
