package com.kii.beehive.portal.web.controller;

import com.kii.beehive.business.service.ThingIFCommandService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.web.entity.ThingCommandRestBean;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/thing-if")
public class ThingIFController extends AbstractThingTagController {

	@Autowired
	private ThingIFCommandService thingIFCommandService;

	/**
	 * send commands to thing list or tag list
	 *
	 * @param restBeanList
	 * @return
	 */
	@RequestMapping(value = "/command", method = {RequestMethod.POST})
	public List<List<Map<String, Object>>> sendCommand(@RequestBody List<ThingCommandRestBean> restBeanList) {

		// construct command request
		List<ExecuteTarget> targets = new ArrayList<>();
		for (ThingCommandRestBean restBean : restBeanList) {
			TagSelector ts = restBean.getSelector();
			if (ts != null && (!CollectionUtils.isEmpty(ts.getTagList()) || !CollectionUtils.isEmpty(ts.getThingList()))) {
				if (!CollectionUtils.isEmpty(ts.getTagList())) {
					List<TagIndex> tags = this.getTags(ts.getTagList());
					tags.forEach(t -> {
						if (!thingTagManager.isTagCreator(t) && !thingTagManager.isTagOwner(t)) {
							throw new BeehiveUnAuthorizedException("not tag creator or owner");
						}
					});
				}

				if (!CollectionUtils.isEmpty(ts.getThingList())) {
					List<String> tempThingList = ts.getThingList().stream().map(String::valueOf).collect(Collectors
							.toList());
					List<GlobalThingInfo> things = this.getThings(tempThingList);
					things.forEach(t -> {
						if (!thingTagManager.isThingCreator(t) && !thingTagManager.isThingOwner(t)) {
							throw new BeehiveUnAuthorizedException("not thing creator or owner");
						}
					});

				}
			} else {
				throw new PortalException("RequiredFieldsMissing", "tagList or ThingList is empty", HttpStatus
						.BAD_REQUEST);
			}

			targets.add(restBean);
		}

		String userID = AuthInfoStore.getUserID();

		// send command request
		List<Map<Long, String>> commandResultList = thingIFCommandService.doCommand(targets, userID);

		// format command response
		List<List<Map<String, Object>>> responseList = new ArrayList<>();

		for (Map<Long, String> commandResult : commandResultList) {

			List<Map<String, Object>> subResponseList = new ArrayList<>();

			Set<Map.Entry<Long, String>> entrySet = commandResult.entrySet();
			for (Map.Entry<Long, String> entry : entrySet) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("globalThingID", entry.getKey());
				map.put("commandID", entry.getValue());

				subResponseList.add(map);
			}

			responseList.add(subResponseList);
		}

		return responseList;
	}


}
