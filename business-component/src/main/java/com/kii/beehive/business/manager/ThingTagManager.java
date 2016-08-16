package com.kii.beehive.business.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.exception.InvalidTriggerFormatException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.PagerTag;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Transactional
public class ThingTagManager {


	@Autowired
	private TagIndexDao tagDao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private ThingLocationRelDao relDao;

	@Autowired
	private ObjectMapper mapper;

	public Set<String> getTagNamesByIDs(List<Long> tagIDs) {


		return tagDao.findByIDs(tagIDs).stream().map(tag -> tag.getFullTagName()).collect(Collectors.toSet());
	}

	public void updateKiicloudRelation(String vendorID, String fullKiiThingID) {
		GlobalThingInfo thing = globalThingDao.getThingByVendorThingID(vendorID);

		if (thing != null) {
			globalThingDao.updateKiiThingID(vendorID, fullKiiThingID);

			/*List<String> locList = new ArrayList<>();
			locList.add(thing.getFullKiiThingID());
			relDao.clearAllRelation(thing.getId());
			relDao.addRelation(thing.getId(), locList);*/
		}
	}

	public void updateState(ThingStatus status, String thingID, String appID) {

		String fullThingID = ThingIDTools.joinFullKiiThingID(appID, thingID);

		try {
			String stateJson = mapper.writeValueAsString(status);
			globalThingDao.updateState(stateJson, fullThingID);

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}

	}


	public GlobalThingInfo getThingByID(long globalThingID) {

		return globalThingDao.findByID(globalThingID);
	}

	public Set<GlobalThingInfo> getThingInfos(TagSelector source) {
		Set<GlobalThingInfo> things = new HashSet<>();

		if (!source.getThingList().isEmpty()) {
			things.addAll(globalThingDao.findByIDs(source.getThingList()));
			return things;
		}

		if (!source.getTagList().isEmpty()) {
			if (StringUtils.isEmpty(source.getType())) {

				if (source.isAndExpress()) {
					things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList()));
				} else {
					things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList()));
				}
			} else {

				if (source.isAndExpress()) {
					things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList(), source.getType()));
				} else {
					things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList(), source.getType()));
				}
			}
		} else {

			throw new InvalidTriggerFormatException(" tag or thing List is null ");

		}

		return things;
	}


	public Set<String> getKiiThingIDs(TagSelector source) {

		Set<GlobalThingInfo> thingList = getThingInfos(source);

		return thingList.stream().map(thing -> thing.getFullKiiThingID()).collect(Collectors.toSet());

	}

	public void iteratorAllThingsStatus(Consumer<GlobalThingInfo> consumer) {

		PagerTag pager = new PagerTag();
		pager.setPageSize(50);
		pager.setStartRow(0);

		List<GlobalThingInfo> list = globalThingDao.getAllThing(pager);


		while (pager.hasNext()) {

			list.forEach(consumer);
			list = globalThingDao.getAllThing(pager);
		}

		list.forEach(consumer);

	}
}
