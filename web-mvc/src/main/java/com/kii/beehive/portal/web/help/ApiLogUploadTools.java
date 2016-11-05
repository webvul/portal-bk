package com.kii.beehive.portal.web.help;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kii.beehive.business.elasticsearch.TaskManager;

/**
 * Created by USER on 11/5/16.
 */
@Component
public class ApiLogUploadTools {

	private static Logger logger = LoggerFactory.getLogger(ApiLogUploadTools.class);

	@Value("${elasticsearch.business.apilog.index}")
	private String ES_INDEX = null;

	@Value("${elasticsearch.business.apilog.indexType}")
	private String ES_INDEX_TYPE = null;

	@Autowired
	private TaskManager transportClientManager;

	@Autowired
	private ObjectMapper mapper;

	private List<JsonNode> bufferList = new ArrayList<>();

	/**
	 * bulk upload api log to ES
	 * this function is scheduled to be called per 3 mins
	 */
	@Scheduled(cron = "0 0/3 * * * ?")
	public void doFlush() {

		List<JsonNode> documentList = new ArrayList<>();

		synchronized (bufferList) {
			documentList.addAll(bufferList);
			bufferList.clear();
		}

		if(!documentList.isEmpty()) {
			transportClientManager.bulkUpload(ES_INDEX, ES_INDEX_TYPE, documentList);
			logger.debug("documentList to ES: " + documentList);
		}
	}

	/**
	 * upload api log to ES
	 * api log will not be uploaded to ES immediately, until the scheduled function "doFlush()" is called automatically
	 *
	 * the expected format of ES document is as below, this function inside will add "timestampe"
	 * {"timestamp":{0}, "apiurl":"{1}", "apimethod":"{2}", "apihttpcode":{3}, "userid":{4}, "token":"{5}"}
	 */
	@Async
	public void upload(String apiUrl, String apimethod, int apihttpcode, long userid, String token) {

		ObjectNode document = mapper.createObjectNode();

		document.put("timestamp", System.currentTimeMillis());
		document.put("apiurl", apiUrl);
		document.put("apimethod", apimethod);
		document.put("apihttpcode", apihttpcode);
		document.put("userid", userid);
		document.put("token", token);

		synchronized (bufferList) {
			bufferList.add(document);
		}

	}

}
