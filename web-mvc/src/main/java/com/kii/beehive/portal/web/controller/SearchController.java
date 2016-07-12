package com.kii.beehive.portal.web.controller;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.elasticsearch.SearchManager;
import com.kii.beehive.business.common.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.entity.SearchRestBean;
import com.kii.beehive.portal.web.exception.PortalException;

@RestController
@RequestMapping(value = "/report")
public class SearchController extends AbstractThingTagController {

	@Autowired
	private SearchManager searchManager;

	@Autowired
	protected TagThingManager thingTagManager;

	/**
	 * POST /report/historical/aggregate
	 *
	 * @param searchRestBean
	 * @return
	 */
	@RequestMapping(value = "/historical/aggregate", method = {RequestMethod.POST})
	public String aggregate(@RequestBody SearchRestBean searchRestBean) {
		if (Strings.isBlank(searchRestBean.getVendorThingID()) || Strings.isBlank(searchRestBean.getIntervalField())
				|| Strings.isBlank(searchRestBean.getOperatorField())
				|| searchRestBean.getStartDate() == null || searchRestBean.getEndDate() == null
				|| searchRestBean.getFields() == null || searchRestBean.getFields().length == 0) {
			throw new PortalException("RequiredFieldsMissing", HttpStatus.BAD_REQUEST);
		}

		GlobalThingInfo thing = thingTagManager.getThingsByVendorThingId(searchRestBean.getVendorThingID());

		if (thing == null) {
			throw EntryNotFoundException.thingNotFound(searchRestBean.getVendorThingID());
		}
		if (!Constants.ADMIN_ID.equals(AuthInfoStore.getUserID())) {//non-admin
			thingTagManager.getAccessibleThingById(AuthInfoStore.getUserIDInLong(), thing.getId());
		}

		String kiiThingID = "thing:" + thing.getFullKiiThingID().substring(thing.getFullKiiThingID().indexOf("-") + 1);

		String queryString = searchManager.queryBuilderForAggs(kiiThingID, searchRestBean.getStartDate(),
				searchRestBean.getEndDate(), searchRestBean.getIntervalField(), searchRestBean.getOperatorField(), searchRestBean
						.getFields());
		String result = searchManager.search(thing, queryString);
		return searchManager.extractResultForAggs(result);
	}

	/**
	 * POST /report/historical
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
			thingTagManager.getAccessibleThingById(AuthInfoStore.getUserIDInLong(), thing.getId());
		}

		String kiiThingID = "thing:" + thing.getFullKiiThingID().substring(thing.getFullKiiThingID().indexOf("-") + 1);

		String queryString = searchManager.queryBuilderForHistorical(kiiThingID, searchRestBean.getStartDate(),
				searchRestBean.getEndDate(), searchRestBean.getSize(), searchRestBean.getFrom());
		String result = searchManager.search(thing, queryString);
		return searchManager.extractResultForHistorical(result);
	}


}
