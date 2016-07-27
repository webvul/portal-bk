package com.kii.beehive.business.elasticsearch.factory;

import javax.annotation.PreDestroy;
import java.util.List;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.kii.beehive.business.data.Action;
import com.kii.beehive.business.data.Field;
import com.kii.beehive.business.elasticsearch.task.AvgTimeParkingSpaceToGatewayTask;
import com.kii.beehive.business.elasticsearch.task.BulkUploadTask;
import com.kii.beehive.business.elasticsearch.task.SearchTask;

/**
 * Created by hdchen on 7/1/16.
 */
@Component
public class ESTaskFactory {
	@Autowired
	private Client indexOpClient;

	@Value("${elasticsearch.business.parkingspace.index}")
	private String bizDataParkingSpaceIndex;

	@Value("${elasticsearch.business.gateway.index}")
	private String bizDataGatewayIndex;

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
		return getSearchTask(new String[]{index}, new String[]{type}, searchType, queryBuilder, null,
				aggregationBuilder, size, from);
	}

	public SearchTask getSearchTask(String[] index, String[] type, SearchType searchType,
									QueryBuilder queryBuilder,
									QueryBuilder postFilter,
									AggregationBuilder aggregationBuilder,
									int size, int from) {
		SearchTask task = new SearchTask();
		task.setClient(indexOpClient);
		task.setIndex_name(index);
		task.setType_name(type);
		task.setSearchType(searchType);
		task.setSize(size);
		task.setFrom(from);
		task.setQueryBuilder(queryBuilder);
		task.setPostFilter(postFilter);
		task.setAggregationBuilder(aggregationBuilder);
		return task;
	}

	public AvgTimeParkingSpaceToGatewayTask getAvgTimeParkingSpaceToGatewayTask(long startTime, long endTime) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.typeQuery(Action.CarOut.name()))
				.must(QueryBuilders.existsQuery(Field.SOURCE))
				.must(QueryBuilders.existsQuery(Field.EVENT_TIME))
				.must(QueryBuilders.rangeQuery(Field.EVENT_TIME).from(startTime).to(endTime)
						.includeLower(true).includeUpper(true));
		TermsBuilder agg = AggregationBuilders.terms(Field.SOURCE)
				.script(new Script("_source." + Field.SOURCE))
				.subAggregation(AggregationBuilders.topHits("top").setSize(10000)
						.addSort(SortBuilders.fieldSort(Field.EVENT_TIME).order(SortOrder.DESC)));
		AvgTimeParkingSpaceToGatewayTask task = new AvgTimeParkingSpaceToGatewayTask();
		task.setClient(indexOpClient);
		task.setIndex_name(bizDataParkingSpaceIndex, bizDataGatewayIndex);
		task.setType_name(Action.CarOut.name());
		task.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		task.setQueryBuilder(boolQueryBuilder);
		task.setAggregationBuilder(agg);
		task.setParkingSpaceIndex(bizDataParkingSpaceIndex);
		task.setGatewayIndex(bizDataGatewayIndex);
		task.setCarIdField(Field.SOURCE);
		return task;
	}
}