package com.kii.beehive.portal.web.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.entity.ExecuteTarget;
import com.kii.beehive.business.entity.TagSelector;
import com.kii.beehive.business.service.ThingIFCommandService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.entity.ThingCommandDetailRestBean;
import com.kii.beehive.portal.web.entity.ThingCommandRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.sdk.entity.thingif.ThingCommand;


@RestController
@RequestMapping(value = "/thing-if")
public class ThingIFController extends AbstractThingTagController {

	private static final String GLOBAL_THING_ID = "globalThingID";

	private static final String COMMAND_ID = "commandID";

	@Autowired
	private ThingIFCommandService thingIFCommandService;

	/**
	 * 发送单条命令(命令目标可以为多个设备)
	 * POST /thing-if/command/single
	 * <p>
	 * refer to doc "Beehive API - Thing-IF API" for request/response details
	 *
	 * @param restBean
	 */
	@RequestMapping(value = "/command/single", method = {RequestMethod.POST})
	public List<Map<String, Object>> sendSingleCommand(@RequestBody ThingCommandRestBean restBean) {

		// construct command request
		List<ExecuteTarget> targets = new ArrayList<>();

		TagSelector ts = restBean.getSelector();
		if (ts != null && (!CollectionUtils.isEmpty(ts.getTagList()) || !CollectionUtils.isEmpty(ts.getThingList()))) {
			if (!CollectionUtils.isEmpty(ts.getTagList())) {
				List<TagIndex> tags = this.getTags(ts.getTagList());
				this.checkPermissionOnTags(tags);
			}

			if (!CollectionUtils.isEmpty(ts.getThingList())) {
				List<String> tempThingList = ts.getThingList().stream().map(String::valueOf).collect(Collectors
						.toList());
				List<GlobalThingInfo> things = this.getThings(tempThingList);
				this.checkPermissionOnThings(things);

			}
		} else {
			PortalException excep = new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "thing command");
			excep.addParam("field", " thingID list or tag list ");
			throw excep;
		}

		targets.add(restBean);


		String userID = AuthInfoStore.getUserIDStr(); //no use

		// send command request
		List<Map<Long, String>> commandResultList = thingIFCommandService.doCommand(targets, userID);

		// format command response
		List<Map<String, Object>> responseList = new ArrayList<>();

		// as targets only has one element, commandResultList is supposed to have only one element too
		for (Map<Long, String> commandResult : commandResultList) {

			List<Map<String, Object>> subResponseList = new ArrayList<>();

			Set<Map.Entry<Long, String>> entrySet = commandResult.entrySet();
			for (Map.Entry<Long, String> entry : entrySet) {
				HashMap<String, Object> map = new HashMap<>();
				map.put(GLOBAL_THING_ID, entry.getKey());
				map.put(COMMAND_ID, entry.getValue());

				subResponseList.add(map);
			}

			responseList.addAll(subResponseList);
		}

		return responseList;
	}

	/**
	 * 发送命令
	 * POST /thing-if/command
	 * <p>
	 * refer to doc "Beehive API - Thing-IF API" for request/response details
	 *
	 * @param restBeanList
	 */
	@RequestMapping(value = "/command", method = {RequestMethod.POST})
	public List<List<Map<String, Object>>> sendCommand(@RequestBody List<ThingCommandRestBean> restBeanList) {

		// construct command request
		List<ExecuteTarget> targets = new ArrayList<>();
		for (ThingCommandRestBean restBean : restBeanList) {
			TagSelector ts = restBean.getSelector();
			if (ts != null && (!CollectionUtils.isEmpty(ts.getTagList()) || !CollectionUtils.isEmpty(ts.getThingList()))) {
				if (!Constants.ADMIN_ID.equals(AuthInfoStore.getUserID())) {
					if (!CollectionUtils.isEmpty(ts.getTagList())) {
						List<TagIndex> tags = this.getTags(ts.getTagList());
						this.checkPermissionOnTags(tags);
					}

					if (!CollectionUtils.isEmpty(ts.getThingList())) {
						List<String> tempThingList = ts.getThingList().stream().map(String::valueOf).collect(Collectors
								.toList());
						List<GlobalThingInfo> things = this.getThings(tempThingList);
						this.checkPermissionOnThings(things);
					}
				}
			} else {

				PortalException excep = new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "thing command");
				excep.addParam("field", " thingID list or tag list ");
				throw excep;
			}

			targets.add(restBean);
		}

		String userID = AuthInfoStore.getUserIDStr(); //no use

		// send command request
		List<Map<Long, String>> commandResultList = thingIFCommandService.doCommand(targets, userID);

		// format command response
		List<List<Map<String, Object>>> responseList = new ArrayList<>();

		for (Map<Long, String> commandResult : commandResultList) {

			List<Map<String, Object>> subResponseList = new ArrayList<>();

			Set<Map.Entry<Long, String>> entrySet = commandResult.entrySet();
			for (Map.Entry<Long, String> entry : entrySet) {
				HashMap<String, Object> map = new HashMap<>();
				map.put(GLOBAL_THING_ID, entry.getKey());
				map.put(COMMAND_ID, entry.getValue());

				subResponseList.add(map);
			}

			responseList.add(subResponseList);
		}

		return responseList;
	}

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

		// if no search body, return empty target
		if (search == null || search.isEmpty()) {
			return responseList;
		}

		// check bad request
		Long globalThingID = this.safeToLong(search.get(GLOBAL_THING_ID));
		Long startDateTime = this.safeToLong(search.get("start"));
		Long endDateTime = this.safeToLong(search.get("end"));

		if (globalThingID == null) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING);
		}

		// check permission
		List<GlobalThingInfo> tempList = this.thingTagManager.getThingsByIds(Arrays.asList(globalThingID));
		GlobalThingInfo thing = CollectUtils.getFirst(tempList);
		if (!thingTagManager.isThingCreator(thing) && !thingTagManager.isThingOwner(thing)) {
			throw new PortalException(ErrorCode.THING_NO_PRIVATE, "thingID", thing.getVendorThingID());
		}

		// get command details
		List<ThingCommand> commandDetailList = thingIFCommandService.queryCommand(thing, startDateTime, endDateTime);
		for (ThingCommand commandDetail : commandDetailList) {
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

		// if no search list, return empty target
		if (!CollectUtils.hasElement(searchList)) {
			return responseList;
		}

		// check bad request
		List<Long> tempThingList = new ArrayList<>();
		for (Map<String, Object> search : searchList) {
			Long globalThingID = this.safeToLong(search.get(GLOBAL_THING_ID));
			String commandID = (String) search.get(COMMAND_ID);

			if (globalThingID == null || Strings.isEmpty(commandID)) {
				throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING);
			}

			tempThingList.add(globalThingID);
		}

		// check permission
		List<GlobalThingInfo> things = thingTagManager.getThingsByIds(tempThingList);
		this.checkPermissionOnThings(things);

		// get command details of each pair of global thing id and command id
		for (GlobalThingInfo thing : things) {
			long globalThingID = thing.getId();
			String commandID = (String) searchList.stream().filter((search) -> this.safeToLong(search.get
					(GLOBAL_THING_ID)).longValue() == globalThingID).findFirst().get().get(COMMAND_ID);

			ThingCommand commandDetail = thingIFCommandService.readCommand(thing, commandID);
			ThingCommandDetailRestBean restBean = new ThingCommandDetailRestBean(thing, commandDetail);
			responseList.add(restBean);
		}

		return responseList;
	}

	/**
	 * throw UNAUTHORIZED exception if neither creator nor owner of any tag
	 *
	 * @param tags
	 */
	private void checkPermissionOnTags(List<TagIndex> tags) {

		if (!CollectUtils.hasElement(tags)) {
			return;
		}

		tags.forEach(t -> {
			if (!thingTagManager.isTagCreator(t) && !thingTagManager.isTagOwner(t)) {
				throw new PortalException(ErrorCode.TAG_NO_PRIVATE, "tagName", t.getFullTagName());
			}
		});

	}

	/**
	 * throw UNAUTHORIZED exception if neither creator nor owner of any thing
	 *
	 * @param things
	 */
	private void checkPermissionOnThings(List<GlobalThingInfo> things) {

		if (!CollectUtils.hasElement(things)) {
			return;
		}

		things.forEach(t -> {
			if (!thingTagManager.isThingCreator(t) && !thingTagManager.isThingOwner(t)) {
				throw new PortalException(ErrorCode.THING_NO_PRIVATE, "thingID", t.getVendorThingID());
			}
		});

	}

	private Long safeToLong(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Integer) {
			return ((Integer) value).longValue();
		} else if (value instanceof Long) {
			return (Long) value;
		} else if (value instanceof String) {
			return Long.valueOf((String) value);
		}

		throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING);
	}

	/**
	 * 查询设备状态
	 * POST /thing-if/states/search
	 * <p>
	 * refer to doc "Beehive API - Thing-IF API" for request/response details
	 *
	 * @param requestMap
	 */
	@RequestMapping(value = "/states/search", method = {RequestMethod.POST})
	public List<Map<String, Object>> searchThingStates(@RequestBody Map<String, Object> requestMap) {

		if (!requestMap.containsKey("thingList")) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING);
		}

		List<Long> thingList = (List<Long>)requestMap.get("thingList");

		List<String> targetStateFieldList = null;
		if(requestMap.containsKey("stateList")) {
			targetStateFieldList = (List<String>)requestMap.get("stateList");
		}


		// check permission
		List<GlobalThingInfo> things = this.thingTagManager.getThingsByIds(thingList);
		this.checkPermissionOnThings(things);


		// set response list
		List<Map<String, Object>> responseList = new ArrayList<>();
		for (GlobalThingInfo thing : things) {
			Map<String, Object> temp = this.getThingState(thing, targetStateFieldList);
			responseList.add(temp);
		}

		return responseList;
	}

	/**
	 * if targetStateFieldList is null or empty, return all fields of the states from thing
	 *
	 * @param thingInfo
	 * @param targetStateFieldList
	 * @return
	 */
	private Map<String, Object> getThingState(GlobalThingInfo thingInfo, List<String> targetStateFieldList) {

		Map<String, Object> result = new HashMap<>();
		result.put("globalThingID", thingInfo.getId());
		result.put("vendorThingID", thingInfo.getVendorThingID());

		// set target states
		Map<String, Object> targetStates = new HashMap<>();
		Map<String, Object> states = thingInfo.getStatus();
		if(states != null) {
			if(targetStateFieldList == null || targetStateFieldList.size() == 0) {
				targetStates.putAll(states);
			} else {
				int size = targetStateFieldList.size();
				for (int i = 0; i < size; i++) {
					String stateField = targetStateFieldList.get(i);
					// keep targetStates not include null value
					if(states.containsKey(stateField)) {
						targetStates.put(stateField, states.get(stateField));
					}
				}
			}
		}

		result.put("states", targetStates);

		return result;
	}


}
