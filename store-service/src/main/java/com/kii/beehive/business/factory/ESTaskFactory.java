package com.kii.beehive.business.factory;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.kii.beehive.business.elasticsearch.task.BulkUploadTask;

/**
 * Created by hdchen on 7/1/16.
 */
@Component
public class ESTaskFactory {

	private Client indexOpClient;

	private Client searchOpClient;

	@Autowired
	public void ESTaskFactory(ESTransportClientFactory esTransportClientFactory) throws IOException {
		indexOpClient = esTransportClientFactory.getTransportClient();
		searchOpClient = esTransportClientFactory.getTransportClient();
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		indexOpClient.close();
		searchOpClient.close();
	}

	public BulkUploadTask getBulkUploadTask(String index, String type, List<JsonNode> documents) {
		BulkUploadTask task = new BulkUploadTask();
		task.setClient(indexOpClient);
		task.setIndex_name(index);
		task.setType_name(type);
		task.setDocs(documents);
		return task;
	}
}