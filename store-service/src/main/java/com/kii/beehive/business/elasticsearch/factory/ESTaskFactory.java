package com.kii.beehive.business.elasticsearch.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.kii.beehive.business.elasticsearch.data.Action;
import com.kii.beehive.business.elasticsearch.data.Field;
import com.kii.beehive.business.elasticsearch.task.AvgTimeParkingSpaceToGatewayTask;
import com.kii.beehive.business.elasticsearch.task.BulkUploadTask;
import com.kii.beehive.business.elasticsearch.task.SearchTask;
import com.kii.beehive.portal.service.BeehiveConfigDao;
import com.kii.beehive.portal.store.entity.es.EsDataSourceCfgEntry;
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
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Created by hdchen on 7/1/16.
 */
@Component
public class ESTaskFactory {

	@Autowired
	private Client indexOpClient;

	@Autowired
	private BeehiveConfigDao cfgDao;

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

		EsDataSourceCfgEntry cfg = cfgDao.getEsConfig();


		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.typeQuery(Action.CarOut.name()))
				.must(QueryBuilders.existsQuery(Field.SOURCE))
				.must(QueryBuilders.existsQuery(Field.EVENT_TIME))
				.must(QueryBuilders.rangeQuery(Field.EVENT_TIME).from(startTime).to(endTime)
						.includeLower(true).includeUpper(true))
				.must(QueryBuilders.boolQuery()
						.should(QueryBuilders.boolQuery().must(
								QueryBuilders.indicesQuery(QueryBuilders.matchAllQuery(), cfg.getBizDataGatewayIndex()))
								.must(QueryBuilders.typeQuery(cfg.getBizDataGatewayIndexTypeLeave()))))
				.should(QueryBuilders.boolQuery().must(
						QueryBuilders.indicesQuery(QueryBuilders.matchAllQuery(), cfg.getBizDataParkingSpaceIndex()))
						.must(QueryBuilders.typeQuery(cfg.getBizDataParkingSpaceIndexTypeLeave())));
		TermsBuilder agg = AggregationBuilders.terms(Field.SOURCE)
				.script(new Script("_source." + cfg.getBizDataCommonCarId()))
				.subAggregation(AggregationBuilders.topHits("top").setSize(10000)
						.addSort(SortBuilders.fieldSort(Field.EVENT_TIME).order(SortOrder.DESC)));
		AvgTimeParkingSpaceToGatewayTask task = new AvgTimeParkingSpaceToGatewayTask();

		task.setClient(indexOpClient);
		task.setIndex_name(cfg.getBizDataParkingSpaceIndex(), cfg.getBizDataGatewayIndex());
		task.setType_name(Action.CarOut.name());
		task.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		task.setQueryBuilder(boolQueryBuilder);
		task.setAggregationBuilder(agg);
		task.setParkingSpaceIndex(cfg.getBizDataParkingSpaceIndex());
		task.setGatewayIndex(cfg.getBizDataGatewayIndex());
		task.setCarIdField(Field.SOURCE);
		return task;
	}
}