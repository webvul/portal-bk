package com.kii.beehive.business.elasticsearch.task;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by JasonChang on 7/5/16.
 */
public class SearchTask extends Task<SearchResponse> {
	private Logger log = LoggerFactory.getLogger(SearchTask.class);
	private String index_name;
	private String type_name;
	private SearchType searchType;
	private QueryBuilder queryBuilder;
	private AggregationBuilder aggregationBuilder;
	private int size;
	private int from;

	public void setIndex_name(String index_name) {
		this.index_name = index_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}


	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	public void setAggregationBuilder(AggregationBuilder aggregationBuilder) {
		this.aggregationBuilder = aggregationBuilder;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	@Override
	protected SearchResponse processRequest(Client client) {

		SearchRequestBuilder searchResponse = client.prepareSearch(index_name).setTypes(type_name);
				/*.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.boolQuery()
						.filter(QueryBuilders.termQuery("target", "thing:th.aba700e36100-37c9-6e11-ecd3-04401dbe"))
						.filter(QueryBuilders.rangeQuery("state.taiwanNo1").from(1467271440000L).to(1467273440000L))
				)
				.setSize(0)
				.addAggregation(AggregationBuilders.dateHistogram("agg").field("state.taiwanNo1").interval
						(DateHistogramInterval.minutes(1))
						.subAggregation(AggregationBuilders.avg("state.humidiy").field("state.humidiy"))
						.subAggregation(AggregationBuilders.avg("state.temprature").field("state.temprature")));*/
		searchResponse.setSearchType(searchType);
		searchResponse.setQuery(queryBuilder);
		if (aggregationBuilder != null) {
			searchResponse.addAggregation(aggregationBuilder);
		}
		searchResponse.setSize(size);
		searchResponse.setFrom(from);

		SearchResponse response = searchResponse.execute().actionGet();

		return response;
	}

	@Override
	protected void handleFailure() {
		log.info("Search failed.");
	}
}
