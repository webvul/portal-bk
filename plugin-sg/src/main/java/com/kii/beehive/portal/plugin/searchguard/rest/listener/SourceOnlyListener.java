package com.kii.beehive.portal.plugin.searchguard.rest.listener;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.action.support.RestResponseListener;
import org.elasticsearch.search.SearchHit;

/**
 * Created by hdchen on 8/23/16.
 */
public class SourceOnlyListener extends RestResponseListener<SearchResponse> {
	public SourceOnlyListener(RestChannel channel) {
		super(channel);
	}

	@Override
	public RestResponse buildResponse(SearchResponse searchResponse) throws Exception {
		final StringBuilder sb = new StringBuilder();
		if (null != searchResponse.getHits() && null != searchResponse.getHits().getHits()
				&& 0 < searchResponse.getHits().getHits().length) {
			final SearchHit[] hits = searchResponse.getHits().getHits();
			for (SearchHit searchHit : hits) {
				if (0 != sb.length()) {
					sb.append(",");
				}
				sb.append(searchHit.getSourceAsString());
			}
		}
		return new BytesRestResponse(searchResponse.status(), "text/json",
				new BytesArray(sb.insert(0, '[').append(']').toString()));
	}
}
