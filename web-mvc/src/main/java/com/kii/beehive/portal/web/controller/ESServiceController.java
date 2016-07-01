package com.kii.beehive.portal.web.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.kii.beehive.business.elasticsearch.TaskManager;

/**
 * Created by hdchen on 6/30/16.
 */

@RestController
@RequestMapping(value = "/es")
public class ESServiceController {

	@Autowired
	private TaskManager transportClientManager;

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
}
