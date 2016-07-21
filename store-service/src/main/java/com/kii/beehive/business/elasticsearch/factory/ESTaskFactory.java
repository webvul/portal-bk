package com.kii.beehive.business.elasticsearch.factory;

import javax.annotation.PreDestroy;

import java.util.List;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import com.kii.beehive.business.elasticsearch.task.BulkUploadTask;
import com.kii.beehive.business.elasticsearch.task.SearchTask;

/**
 * Created by hdchen on 7/1/16.
 */
@Component
public class ESTaskFactory {


	@Autowired
	private Client indexOpClient;


	@PreDestroy
	public void cleanUp() throws Exception {
		indexOpClient.close();
	}


	public BulkUploadTask getBulkUploadTask(String index, String type, List<JsonNode> documents) {
		BulkUploadTask task = new BulkUploadTask();
		task.setClient(indexOpClient);
		task.setIndex_name(index);
		task.setType_name(type);
		task.setDocs(documents);
		return task;
	}

	public SearchTask getSearchTask(String index, String type, SearchType searchType, QueryBuilder queryBuilder,
									AggregationBuilder aggregationBuilder, int size, int from) {
		SearchTask task = new SearchTask();
		task.setClient(indexOpClient);
		task.setIndex_name(index);
		task.setType_name(type);
		task.setSearchType(searchType);
		task.setSize(size);
		task.setFrom(from);
		task.setQueryBuilder(queryBuilder);
		task.setAggregationBuilder(aggregationBuilder);
		return task;
	}
}