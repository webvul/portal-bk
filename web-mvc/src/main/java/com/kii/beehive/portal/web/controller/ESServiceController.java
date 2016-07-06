package com.kii.beehive.portal.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kii.beehive.business.elasticsearch.TaskManager;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.entity.SearchRestBean;
import com.kii.beehive.portal.web.exception.PortalException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
	 * POST /es/historical/aggregate
	 *
	 * @return
	 */
	@RequestMapping(value = "/historical/aggregate", method = {RequestMethod.POST})
	public String aggregate(@RequestBody SearchRestBean searchRestBean) {

		if (Strings.isBlank(searchRestBean.getVendorThingID()) || Strings.isBlank(searchRestBean.getIntervalField())
				|| Strings.isBlank(searchRestBean.getOperatorField())
				|| searchRestBean.getStartDate() == null || searchRestBean.getEndDate() == null
				|| searchRestBean.getFields() == null || searchRestBean.getFields().length == 0
				|| searchRestBean.getUnit() == 0) {
			throw new PortalException("RequiredFieldsMissing", HttpStatus.BAD_REQUEST);
		}

		GlobalThingInfo thing = thingTagManager.getThingsByVendorThingId(searchRestBean.getVendorThingID());

		if (thing == null) {
			throw EntryNotFoundException.thingNotFound(searchRestBean.getVendorThingID());
		}
		if (!Constants.ADMIN_ID.equals(AuthInfoStore.getUserID())) {//non-admin
			thingTagManager.getAccessibleThingById(AuthInfoStore.getUserID(), thing.getId());
		}

		String kiiThingID = "thing:" + thing.getFullKiiThingID().substring(thing.getFullKiiThingID().indexOf("-") + 1);

		String r = transportClientManager.queryBuilderForAggs(thing.getKiiAppID(), "spark",
				kiiThingID,
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
			throw new PortalException("RequiredFieldsMissing", HttpStatus.BAD_REQUEST);
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
