package com.kii.beehive.business.factory;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.kii.beehive.business.elasticsearch.TransportClientProvider;
import com.kii.beehive.business.elasticsearch.task.BulkUploadTask;

/**
 * Created by hdchen on 7/1/16.
 */
@Component
public class ESTaskFactory {

	private ArrayBlockingQueue<Client> indexOpClients;

	private ArrayBlockingQueue<Client> searchOpClients;

	@Autowired
	public void ESTaskFactory(ESTransportClientFactory esTransportClientFactory,
							  @Value("${elasticsearch.indexClient.num}") int indexclient,
							  @Value("${elasticsearch.searchClient.num}") int searchclient) throws IOException {
		indexOpClients = new ArrayBlockingQueue(indexclient);
		searchOpClients = new ArrayBlockingQueue(searchclient);
		for (int i = 0; i < indexclient; ++i) {
			indexOpClients.offer(esTransportClientFactory.getTransportClient());
		}
		for (int i = 0; i < searchclient; ++i) {
			searchOpClients.offer(esTransportClientFactory.getTransportClient());
		}
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		while (!indexOpClients.isEmpty()) {
			Client client = indexOpClients.poll();
			client.close();
		}
		while (!searchOpClients.isEmpty()) {
			Client client = searchOpClients.poll();
			client.close();
		}
	}

	public BulkUploadTask getBulkUploadTask(String index, String type, List<JsonNode> documents) {
		BulkUploadTask task = new BulkUploadTask();
		task.setClientProvider(getIndexOpClientProvider());
		task.setIndex_name(index);
		task.setType_name(type);
		task.setDocs(documents);
		return task;
	}

	private TransportClientProvider getIndexOpClientProvider() {
		return new TransportClientProvider() {
			@Override
			public Client get() {
				return indexOpClients.poll();
			}

			@Override
			public void release(Client client) {
				indexOpClients.offer(client);
			}
		};
	}

	private TransportClientProvider getSearchOpClientProvider() {
		return new TransportClientProvider() {
			@Override
			public Client get() {
				return searchOpClients.poll();
			}

			@Override
			public void release(Client client) {
				searchOpClients.offer(client);
			}
		};
	}
}