package com.kii.beehive.portal.manager;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamTagRelationDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.TagUserRelation;
import com.kii.beehive.portal.jdbc.entity.TeamTagRelation;

@Component
@Transactional
public class TagManager {


	@Autowired
	private TagIndexDao tagIndexDao;


	@Autowired
	private TagUserRelationDao tagUserRelationDao;


	@Autowired
	private TeamTagRelationDao teamTagRelationDao;

	public Long  createCustomTag(TagIndex tag){


		if (null != tag.getId()) {
			TagIndex existedTag = getTagIndexes(Arrays.asList(tag.getId().toString())).get(0);
			if (existedTag.getCreateBy().equals(AuthInfoStore.getUserIDStr())) {
				throw new UnauthorizedException(UnauthorizedException.NOT_TAG_CREATER);
			}

		} else {

			boolean sign= tagIndexDao.findTagIdsByTeamAndTagTypeAndName(AuthInfoStore.getTeamID(),  TagType.Custom, tag.getDisplayName());

			if (!sign) {
				throw new UnauthorizedException(UnauthorizedException.NOT_TAG_CREATER);
			}
		}


		tag.setTagType(TagType.Custom);
		tag.setFullTagName(TagType.Custom.getTagName(tag.getDisplayName()));


		Long tagID = tagIndexDao.saveOrUpdate(tag);

		if (AuthInfoStore.getTeamID() != null) {
			teamTagRelationDao.saveOrUpdate(new TeamTagRelation(AuthInfoStore.getTeamID(), tagID));
		}

		tagUserRelationDao.saveOrUpdate(new TagUserRelation(tagID, AuthInfoStore.getUserID()));

		return tagID;

	}




	private List<TagIndex> getTagIndexes(List<String> tagIDList)  {
		List<Long> tagIds = tagIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).map(Long::valueOf)
				.collect(Collectors.toList());
		List<TagIndex> tagIndexes = tagIndexDao.findByIDs(tagIds);
		if ( tagIndexes.isEmpty() || !tagIndexes.stream().map(TagIndex::getId).map(Object::toString).
				collect(Collectors.toSet()).containsAll(tagIDList)) {
			tagIds.removeAll(tagIndexes.stream().map(TagIndex::getId).collect(Collectors.toList()));
			throw EntryNotFoundException.existsNullTag(tagIds);
		}
		return tagIndexes;
	}



}

