package com.kii.beehive.portal.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kii.beehive.business.elasticsearch.TaskManager;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.entity.SearchRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by hdchen on 6/30/16.
 */

@RestController
@RequestMapping(value = "/es")
public class ESServiceController {

	@Autowired
	private TaskManager transportClientManager;

	@Autowired
	protected TagThingManager thingTagManager;

	/**
	 * POST /es/bulkUpload/{appId}/{vendorThingId}
	 *
	 * @param documents
	 * @return
	 */
	@RequestMapping(value = "/bulkUpload/{appId}/{vendorThingId}", method = {RequestMethod.POST})
	public void bulkUpload(@PathVariable("appId") String appId,
						   @PathVariable("vendorThingId") String vendorThingId,
						   @RequestBody List<JsonNode> documents) {
		transportClientManager.bulkUpload(appId, vendorThingId, documents);
	}

	/**
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/avgTimeParkingSpaceToGateway/{startTime}/{endTime}", method = {RequestMethod.POST})
	public double getAvgTimeParkingSpaceToGateway(@PathVariable("startTime") long startTime,
												  @PathVariable("endTime") long endTime) {
		try {
			return transportClientManager.getAvgTimeParkingSpaceToGateway(startTime, endTime);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}


	/**
	 * POST /es/historical/aggregate
	 *
	 * @return
	 */
	@RequestMapping(value = "/historical/aggregate", method = {RequestMethod.POST})
	public String aggregate(@RequestBody SearchRestBean searchRestBean) {

		if (searchRestBean.getVendorThingIDs().length == 0 || Strings.isBlank(searchRestBean.getIntervalField())
				|| Strings.isBlank(searchRestBean.getOperatorField())
				|| searchRestBean.getStartDate() == null || searchRestBean.getEndDate() == null
				|| searchRestBean.getFields() == null || searchRestBean.getFields().length == 0
				|| searchRestBean.getUnit() == 0) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "aggregate");
		}

		List<GlobalThingInfo> things = thingTagManager.getThingsByVendorThingIds(Arrays.asList(searchRestBean.getVendorThingIDs()));

		if (things.size() == 0) {
			throw EntryNotFoundException.thingNotFound(Arrays.toString(searchRestBean.getVendorThingIDs()));
		}

		List<String> kiiThingIDs = new ArrayList<String>();

		things.forEach(thingInfo -> {
			if (!Constants.ADMIN_ID.equals(AuthInfoStore.getUserID())) {//non-admin
				thingTagManager.getAccessibleThingById(AuthInfoStore.getUserID(), thingInfo.getId());
			}

			kiiThingIDs.add("thing:" + thingInfo.getFullKiiThingID().substring(thingInfo.getFullKiiThingID().indexOf("-") + 1));
		});

		if (kiiThingIDs.size() == 0) {
			throw EntryNotFoundException.thingNotFound(Arrays.toString(searchRestBean.getVendorThingIDs()));
		}


		String r = transportClientManager.queryBuilderForAggs(things.get(0).getKiiAppID(), "spark",
				kiiThingIDs.toArray(new String[kiiThingIDs.size()]),
				searchRestBean.getStartDate(), searchRestBean.getEndDate(), searchRestBean.getIntervalField(),
				searchRestBean.getUnit(), searchRestBean.getOperatorField(), searchRestBean.getFields());

		return r;
	}

	/**
	 * POST /es/historical
	 *
	 * @param searchRestBean
	 * @return
	 */
	@RequestMapping(value = "/historical", method = {RequestMethod.POST})
	public String historical(@RequestBody SearchRestBean searchRestBean) {
		if (Strings.isBlank(searchRestBean.getVendorThingID())
				|| searchRestBean.getStartDate() == null || searchRestBean.getEndDate() == null
				|| searchRestBean.getSize() == 0) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "historical");
		}

		if (searchRestBean.getSize() > 100) {
			searchRestBean.setSize(100);
		}

		GlobalThingInfo thing = thingTagManager.getThingsByVendorThingId(searchRestBean.getVendorThingID());

		if (thing == null) {
			throw EntryNotFoundException.thingNotFound(searchRestBean.getVendorThingID());
		}
		if (!Constants.ADMIN_ID.equals(AuthInfoStore.getUserID())) {//non-admin
			thingTagManager.getAccessibleThingById(AuthInfoStore.getUserID(), thing.getId());
		}

		String kiiThingID = "thing:" + thing.getFullKiiThingID().substring(thing.getFullKiiThingID().indexOf("-") + 1);

		String result = transportClientManager.queryBuilderForHistorical(thing.getKiiAppID(), "spark", kiiThingID,
				searchRestBean.getStartDate(),
				searchRestBean.getEndDate(), searchRestBean.getSize(), searchRestBean.getFrom());
		return result;
	}
}
