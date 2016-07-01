package com.kii.beehive.business.elasticsearch.task;

import java.util.List;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by hdchen on 7/1/16.
 */
public class BulkUploadTask extends Task<BulkResponse> {
	private Logger log = LoggerFactory.getLogger(BulkUploadTask.class);
	private String index_name;
	private String type_name;
	private List<JsonNode> docs;

	public void setIndex_name(String index_name) {
		this.index_name = index_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public void setDocs(List<JsonNode> docs) {
		this.docs = docs;
	}

	@Override
	protected BulkResponse processRequest(Client client) {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for (JsonNode node : docs) {
			bulkRequest.add(client.prepareIndex(index_name, type_name)
					.setContentType(XContentType.JSON)
					.setSource(node.toString()));
		}
		BulkResponse response = bulkRequest.execute().actionGet();
		if (response.hasFailures()) {
			handleFailure();
		}
		return response;
	}

	@Override
	protected void handleFailure() {
		log.info("Bulk upload thing status failed. Start printing all documents.");
		for (JsonNode node : docs) {
			log.info("[Document] " + node.toString());
		}
	}
}
