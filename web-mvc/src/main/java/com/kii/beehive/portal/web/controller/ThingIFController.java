package com.kii.beehive.portal.web.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.service.ThingIFCommandService;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.entity.ThingCommandDetailRestBean;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.sdk.entity.thingif.CommandDetail;


@RestController
@RequestMapping(value = "/thing-if")
public class ThingIFController extends AbstractThingTagController {

	private static final String GLOBAL_THING_ID = "globalThingID";

	private static final String COMMAND_ID = "commandID";

	@Autowired
	private ThingIFCommandService thingIFCommandService;

	/**
	 * 查询命令详细（单个设备）
	 * POST POST /thing-if/command/list
	 * <p>
	 * refer to doc "Beehive API - Thing-IF API" for request/response details
	 *
	 * @param search
	 */
	@RequestMapping(value = "/command/list", method = {RequestMethod.POST})
	public List<ThingCommandDetailRestBean> getCommandDetailsOfSingleThing(@RequestBody Map<String, Object>
																				 search) {

		List<ThingCommandDetailRestBean> responseList = new ArrayList<>();

		// if no search body, return empty result
		if(search == null || search.isEmpty()){
			return responseList;
		}

		// check bad request
		Long globalThingID = this.safeToLong(search.get(GLOBAL_THING_ID));
		Long startDateTime = this.safeToLong(search.get("start"));
		Long endDateTime = this.safeToLong(search.get("end"));

		if(globalThingID == null) {
			throw new PortalException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
		}

		// check permission
		List<GlobalThingInfo> tempList = this.thingTagManager.getThingsByIds(Arrays.asList(globalThingID));
		GlobalThingInfo thing = CollectUtils.getFirst(tempList);
		if (!thingTagManager.isThingCreator(thing) && !thingTagManager.isThingOwner(thing)) {
			throw new PortalException(ErrorCode.THING_NO_PRIVATE,HttpStatus.UNAUTHORIZED);
		}

		// get command details
		List<CommandDetail> commandDetailList = thingIFCommandService.queryCommand(thing, startDateTime, endDateTime);
		for(CommandDetail commandDetail : commandDetailList) {
			ThingCommandDetailRestBean restBean = new ThingCommandDetailRestBean(thing, commandDetail);
			responseList.add(restBean);
		}

		return responseList;
	}

	/**
	 * 查询命令详细
	 * POST /thing-if/command/search
	 * <p>
	 * refer to doc "Beehive API - Thing-IF API" for request/response details
	 *
	 * @param searchList
	 * @return
	 */
	@RequestMapping(value = "/command/search", method = {RequestMethod.POST})
	public List<ThingCommandDetailRestBean> getCommandDetails(@RequestBody List<Map<String, Object>> searchList) {

		List<ThingCommandDetailRestBean> responseList = new ArrayList<>();

		// if no search list, return empty result
		if(!CollectUtils.hasElement(searchList)){
			return responseList;
		}

		// check bad request
		List<Long> tempThingList = new ArrayList<>();
		for (Map<String, Object> search : searchList) {
			Long globalThingID = this.safeToLong(search.get(GLOBAL_THING_ID));
			String commandID = (String)search.get(COMMAND_ID);

			if(globalThingID == null || Strings.isEmpty(commandID)) {
				throw new PortalException(ErrorCode.BAD_REQUEST,HttpStatus.BAD_REQUEST);
			}

			tempThingList.add(globalThingID);
		}

		// check permission
		List<GlobalThingInfo> things = thingTagManager.getThingsByIds(tempThingList);
		this.checkPermissionOnThings(things);

		// get command details of each pair of global thing id and command id
		for (GlobalThingInfo thing : things) {
			long globalThingID = thing.getId();
			String commandID = (String)searchList.stream().filter((search) -> this.safeToLong(search.get
					(GLOBAL_THING_ID)).longValue() == globalThingID).findFirst().get().get(COMMAND_ID);

			CommandDetail commandDetail = thingIFCommandService.readCommand(thing, commandID);
			ThingCommandDetailRestBean restBean = new ThingCommandDetailRestBean(thing, commandDetail);
			responseList.add(restBean);
		}

		return responseList;
	}

	/**
	 * throw UNAUTHORIZED exception if neither creator nor owner of any tag
	 * @param tags
	 */
	private void checkPermissionOnTags(List<TagIndex> tags) {

		if(!CollectUtils.hasElement(tags)){
			return;
		}

		tags.forEach(t -> {
			if (!thingTagManager.isTagCreator(t) && !thingTagManager.isTagOwner(t)) {
				throw new PortalException(ErrorCode.TAG_NO_PRIVATE,HttpStatus.UNAUTHORIZED);
			}
		});

	}

	/**
	 * throw UNAUTHORIZED exception if neither creator nor owner of any thing
	 * @param things
	 */
	private void checkPermissionOnThings(List<GlobalThingInfo> things) {

		if(!CollectUtils.hasElement(things)){
			return;
		}

		things.forEach(t -> {
			if (!thingTagManager.isThingCreator(t) && !thingTagManager.isThingOwner(t)) {
				throw new PortalException(ErrorCode.THING_NO_PRIVATE,HttpStatus.UNAUTHORIZED);
			}
		});

	}

	private Long safeToLong(Object value) {
		if(value == null) {
			return null;
		}

		if(value instanceof Integer) {
			return ((Integer) value).longValue();
		} else if(value instanceof Long) {
			return (Long) value;
		} else if(value instanceof String) {
			return Long.valueOf((String)value);
		}

		throw new PortalException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
	}


}
