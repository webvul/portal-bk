package com.kii.beehive.portal.plugin.searchguard.rest;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import java.util.Objects;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.search.RestSearchAction;
import org.elasticsearch.rest.action.support.RestStatusToXContentListener;
import com.kii.beehive.portal.plugin.searchguard.rest.listener.SourceOnlyListener;

/**
 * Created by hdchen on 8/23/16.
 */
public class SearchAction extends BaseRestHandler {
	private static final String CONTEXT_ROOT = "beehive.es.api.context.root";

	@Inject
	public SearchAction(Settings settings, RestController controller, Client client) {
		super(settings, controller, client);
		final String contextPath = settings.get(CONTEXT_ROOT);
		Objects.requireNonNull(contextPath, "Context path should not be null");
		controller.registerHandler(GET, contextPath + "/_search", this);
		controller.registerHandler(POST, contextPath + "/_search", this);
		controller.registerHandler(GET, contextPath + "/{index}/_search", this);
		controller.registerHandler(POST, contextPath + "/{index}/_search", this);
		controller.registerHandler(GET, contextPath + "/{index}/{type}/_search", this);
		controller.registerHandler(POST, contextPath + "/{index}/{type}_search", this);
	}

	@Override
	protected void handleRequest(RestRequest restRequest, RestChannel restChannel, Client client) throws Exception {
		final boolean sourceOnly = restRequest.paramAsBoolean("sourceOnly", false);
		SearchRequest searchRequest = new SearchRequest();
		RestSearchAction.parseSearchRequest(searchRequest, restRequest, parseFieldMatcher, (BytesReference) null);
		client.search(searchRequest,
				sourceOnly ? new SourceOnlyListener(restChannel) : new RestStatusToXContentListener(restChannel));
	}
}
