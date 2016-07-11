package com.kii.beehive.business.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.factory.ESTaskFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by hdchen on 6/30/16.
 */
@Component
public class TaskManager {

	private final ESTaskFactory taskFactory;

	private final ThreadPoolTaskExecutor indexThreadPoolTaskExecutor;

	private final ThreadPoolTaskExecutor searchThreadPoolTaskExecutor;

	private final String DATE_FIELD = "state.taiwanNo1";
	private final String TERM_FIELD = "target";

	@Autowired
	public TaskManager(@Value("${elasticsearch.indexTask.corePoolSize}") int indexTaskPoolSize,
					   @Value("${elasticsearch.indexTask.maxPoolSize}") int indexTaskMaxSize,
					   @Value("${elasticsearch.searchTask.corePoolSize}") int searchTaskPoolSize,
					   @Value("${elasticsearch.searchTask.maxPoolSize}") int searchTaskMaxSize,
					   @Value("${elasticsearch.taskManager.waitForTasksToCompleteOnShutdown}")
							   boolean waitForTasksToCompleteOnShutdown,
					   ESTaskFactory esTaskFactory) throws IOException {
		taskFactory = esTaskFactory;
		indexThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		indexThreadPoolTaskExecutor.setCorePoolSize(indexTaskPoolSize);
		indexThreadPoolTaskExecutor.setMaxPoolSize(indexTaskMaxSize);
		indexThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
		indexThreadPoolTaskExecutor.initialize();
		searchThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		searchThreadPoolTaskExecutor.setCorePoolSize(searchTaskPoolSize);
		searchThreadPoolTaskExecutor.setMaxPoolSize(searchTaskMaxSize);
		searchThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
		searchThreadPoolTaskExecutor.initialize();
	}

	public void bulkUpload(String index, String type, List<JsonNode> data) {
		indexThreadPoolTaskExecutor.submit(taskFactory.getBulkUploadTask(index, type, data));
	}

	public String queryBuilderForAggs(String index, String type, String[] kiiThingIDs, long startDate, long endDate,
									  String intervalField, int unit, String operatorField, String[] avgFields) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();

		for (String kiiThingID : kiiThingIDs) {
			qb = qb.should(QueryBuilders.termQuery(TERM_FIELD, kiiThingID));
		}

		qb = qb.filter(QueryBuilders.rangeQuery(DATE_FIELD).from(startDate).to(endDate));


		DateHistogramInterval di = null;

		switch (intervalField) {
			case "m":
				di = DateHistogramInterval.minutes(unit);
				break;
			case "d":
				di = DateHistogramInterval.days(unit);
				break;
			case "h":
				di = DateHistogramInterval.hours(unit);
				break;
			case "s":
				di = DateHistogramInterval.seconds(unit);
				break;
			default:
				throw new IllegalArgumentException("Invalid intervalField = " + intervalField);
		}

		AggregationBuilder ab = AggregationBuilders.dateHistogram("agg").field(DATE_FIELD).interval(di);

		for (String avgField : avgFields) {
			switch (operatorField) {
				case "avg":
					ab = ab.subAggregation(AggregationBuilders.avg(avgField).field(avgField));
					break;
				case "min":
					ab = ab.subAggregation(AggregationBuilders.min(avgField).field(avgField));
					break;
				case "max":
					ab = ab.subAggregation(AggregationBuilders.max(avgField).field(avgField));
					break;
				case "sum":
					ab = ab.subAggregation(AggregationBuilders.sum(avgField).field(avgField));
					break;
				default:
					throw new IllegalArgumentException("Invalid operatorField = " + operatorField);
			}
		}

		String result = null;

		Future f = searchThreadPoolTaskExecutor.submit(taskFactory.getSearchTask(index, type, SearchType
				.DFS_QUERY_THEN_FETCH, qb, ab, 0, 0));
		try {
			SearchResponse s = (SearchResponse) f.get();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(s.toString());
			result = actualObj.get("aggregations").get("agg").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public String queryBuilderForHistorical(String index, String type, String kiiThingID, long startDate, long
			endDate, int size, int from) {

		QueryBuilder qb = QueryBuilders.boolQuery()
				.should(QueryBuilders.termQuery(TERM_FIELD, kiiThingID))
				.filter(QueryBuilders.rangeQuery(DATE_FIELD).from(startDate).to(endDate));

		Future f = searchThreadPoolTaskExecutor.submit(taskFactory.getSearchTask(index, type, SearchType
				.DFS_QUERY_THEN_FETCH, qb, null, size, from));

		String result = null;
		try {
			SearchResponse s = (SearchResponse) f.get();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(s.toString());
			result = actualObj.get("hits").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
