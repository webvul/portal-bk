package com.kii.beehive.business.elasticsearch;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.elasticsearch.factory.ESTaskFactory;
import com.kii.beehive.business.elasticsearch.task.AvgTimeParkingSpaceToGatewayTask;

/**
 * Created by hdchen on 6/30/16.
 */
@Component
public class TaskManager {

	private final ESTaskFactory taskFactory;

	private final ThreadPoolTaskExecutor indexThreadPoolTaskExecutor;

	private final ThreadPoolTaskExecutor searchThreadPoolTaskExecutor;

	@Autowired
	private ObjectMapper mapper;

	//private final String DATE_FIELD = "state.taiwanNo1";
	private final String TERM_FIELD = "task";

	@Autowired
	public TaskManager(@Value("${elasticsearch.indexTask.corePoolSize:2}") int indexTaskPoolSize,
					   @Value("${elasticsearch.indexTask.maxPoolSize:5}") int indexTaskMaxSize,
					   @Value("${elasticsearch.searchTask.corePoolSize:3}") int searchTaskPoolSize,
					   @Value("${elasticsearch.searchTask.maxPoolSize:10}") int searchTaskMaxSize,
					   @Value("${elasticsearch.taskManager.waitForTasksToCompleteOnShutdown:true}")
							   boolean waitForTasksToCompleteOnShutdown,
					   ESTaskFactory esTaskFactory) throws IOException {
		taskFactory = esTaskFactory;
		indexThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		indexThreadPoolTaskExecutor.setCorePoolSize(indexTaskPoolSize);
		indexThreadPoolTaskExecutor.setMaxPoolSize(indexTaskMaxSize);
		indexThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
		indexThreadPoolTaskExecutor.setThreadGroupName("ES-IndexOp");
		indexThreadPoolTaskExecutor.setThreadNamePrefix("ES-IndexOp-");
		indexThreadPoolTaskExecutor.setQueueCapacity(10000);
		indexThreadPoolTaskExecutor.setKeepAliveSeconds(600);
		indexThreadPoolTaskExecutor.setDaemon(true);
		indexThreadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		indexThreadPoolTaskExecutor.initialize();
		searchThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		searchThreadPoolTaskExecutor.setCorePoolSize(searchTaskPoolSize);
		searchThreadPoolTaskExecutor.setMaxPoolSize(searchTaskMaxSize);
		searchThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
		searchThreadPoolTaskExecutor.setThreadGroupName("ES-SearchOp");
		searchThreadPoolTaskExecutor.setThreadNamePrefix("ES-SearchOp-");
		searchThreadPoolTaskExecutor.setQueueCapacity(10000);
		searchThreadPoolTaskExecutor.setKeepAliveSeconds(600);
		searchThreadPoolTaskExecutor.setDaemon(true);
		searchThreadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		searchThreadPoolTaskExecutor.initialize();
	}

	public void bulkUpload(String index, String type, List<JsonNode> data) {
		indexThreadPoolTaskExecutor.submit(taskFactory.getBulkUploadTask(index, type, data));
	}

	public double getAvgTimeParkingSpaceToGateway(long startTime, long endTime) throws ExecutionException,
			InterruptedException {
		AvgTimeParkingSpaceToGatewayTask task = taskFactory.getAvgTimeParkingSpaceToGatewayTask(startTime, endTime);
		searchThreadPoolTaskExecutor.submit(task).get();
		return task.getAverageTime();
	}

	public String queryBuilderForAggs(String index, String type, String[] kiiThingIDs, long startDate, long endDate,
									  String intervalField, String dateField, int unit, String operatorField, String[]
											  avgFields) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();

		for (String kiiThingID : kiiThingIDs) {
			qb = qb.should(QueryBuilders.termQuery(TERM_FIELD, kiiThingID));
		}

		qb = qb.filter(QueryBuilders.rangeQuery(dateField).from(startDate).to(endDate));


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

		AggregationBuilder ab = AggregationBuilders.dateHistogram("agg").field(dateField).interval(di);

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
				.DFS_QUERY_THEN_FETCH, qb, ab, 0, 0, null, null));
		try {
			SearchResponse s = (SearchResponse) f.get();

//			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(s.toString());
			result = actualObj.get("aggregations").get("agg").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public String queryBuilderForHistorical(String index, String type, String kiiThingID, String dateField, long startDate, long
			endDate, int size, int from, String orderField, String order) {

		QueryBuilder qb = QueryBuilders.boolQuery()
				.should(QueryBuilders.termQuery(TERM_FIELD, kiiThingID))
				.filter(QueryBuilders.rangeQuery(dateField).from(startDate).to(endDate));

		SortOrder so = SortOrder.ASC;
		if ("desc".equals(order)) {
			so = SortOrder.DESC;
		}

		Future f = searchThreadPoolTaskExecutor.submit(taskFactory.getSearchTask(index, type, SearchType
				.DFS_QUERY_THEN_FETCH, qb, null, size, from, orderField, so));

		String result = null;
		try {
			SearchResponse s = (SearchResponse) f.get();
//			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(s.toString());
			result = actualObj.get("hits").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
