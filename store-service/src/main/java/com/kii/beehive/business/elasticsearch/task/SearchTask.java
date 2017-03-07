package com.kii.beehive.business.elasticsearch.task;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by JasonChang on 7/5/16.
 */
public class SearchTask extends Task<SearchResponse> {
	private Logger log = LoggerFactory.getLogger(SearchTask.class);
	private String[] index_name;
	private String[] type_name;
	private SearchType searchType = SearchType.DFS_QUERY_THEN_FETCH;
	private QueryBuilder queryBuilder;
	private AggregationBuilder[] aggregationBuilder;
	private Integer size;
	private Integer from;
	private SortOrder order;
	private String orderField;
	private QueryBuilder postFilter;

	public void setIndex_name(String... index_name) {
		this.index_name = index_name;
	}

	public void setType_name(String... type_name) {
		this.type_name = type_name;
	}


	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	public void setAggregationBuilder(AggregationBuilder... aggregationBuilder) {
		this.aggregationBuilder = aggregationBuilder;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public void setOrder(SortOrder order) {
		this.order = order;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	@Override
	protected SearchResponse processRequest(Client client) {

		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index_name).setTypes(type_name);
		searchRequestBuilder.setSearchType(searchType);
		searchRequestBuilder.setQuery(queryBuilder);
		searchRequestBuilder.setPostFilter(postFilter);
		if (null != aggregationBuilder) {
			for (AggregationBuilder agg : aggregationBuilder) {
				if (null != agg) {
					searchRequestBuilder.addAggregation(agg);
				}
			}
		}
		if (null != size) {
			searchRequestBuilder.setSize(size);
		}
		if (null != from) {
			searchRequestBuilder.setFrom(from);
		}
		if (!StringUtils.isEmpty(orderField)) {
			searchRequestBuilder.addSort(orderField, order);
		}
		SearchResponse response = searchRequestBuilder.execute().actionGet();

		return response;
	}

	@Override
	protected void handleFailure() {
		log.info("Search failed.");
	}

	public void setPostFilter(QueryBuilder postFilter) {
		this.postFilter = postFilter;
	}
}
