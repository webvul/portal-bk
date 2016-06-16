package com.kii.beehive.portal.web.controller;

import com.kii.beehive.business.elasticsearch.SearchManager;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.entity.SearchRestBean;
import com.kii.beehive.portal.web.exception.PortalException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/report")
public class SearchController extends AbstractThingTagController {

	@Autowired
	private SearchManager searchManager;

	@Autowired
	protected TagThingManager thingTagManager;

	/**
	 * GET /report/historical
	 *
	 * @param searchRestBean
	 * @return
	 */
	@RequestMapping(value = "/historical", method = {RequestMethod.POST})
	public String historical(@RequestBody SearchRestBean searchRestBean) {
		if (Strings.isBlank(searchRestBean.getVenderThingID()) || Strings.isBlank(searchRestBean.getIntervalField())
				|| searchRestBean.getStartDate() == null || searchRestBean.getEndDate() == null
				|| searchRestBean.getAvgFields() == null || searchRestBean.getAvgFields().length == 0) {
			throw new PortalException("RequiredFieldsMissing", HttpStatus.BAD_REQUEST);
		}

		GlobalThingInfo thing = thingTagManager.getThingsByVendorThingId(searchRestBean.getVenderThingID());

		if (thing == null) {
			throw EntryNotFoundException.thingNotFound(searchRestBean.getVenderThingID());
		}
		if (!"211102".equals(AuthInfoStore.getUserID())) {//non-admin
			thingTagManager.getAccessibleThingById(AuthInfoStore.getUserID(), thing.getId());
		}

		String queryString = searchManager.queryBuilder(searchRestBean.getVenderThingID(), searchRestBean.getStartDate(),
				searchRestBean.getEndDate(), searchRestBean.getIntervalField(), searchRestBean.getAvgFields());
		String result = searchManager.search(thing, queryString);
		return result;
	}


}