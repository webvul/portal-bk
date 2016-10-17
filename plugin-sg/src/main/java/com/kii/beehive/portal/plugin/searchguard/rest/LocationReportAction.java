package com.kii.beehive.portal.plugin.searchguard.rest;

import static org.elasticsearch.rest.RestRequest.Method.POST;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.search.RestSearchAction;
import org.elasticsearch.rest.action.support.RestActions;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import com.kii.beehive.client.RestClient;
import com.kii.beehive.client.RestClientBuilder;
import com.kii.beehive.client.dto.ThingInfo;
import com.kii.beehive.client.request.thing.LocationRequestBuilder;
import com.kii.beehive.portal.plugin.searchguard.rest.listener.SourceOnlyListener;

/**
 * Created by hdchen on 10/14/16.
 */
public class LocationReportAction extends BaseRestHandler {
	private static final String CONTEXT_ROOT = "beehive.es.api.context.root";

	private static final String BEEHIVE_API_HOST = "beehive.api.host";

	private static final String BEEHIVE_API_PORT = "beehive.api.port";

	private static final String BEEHIVE_API_CONTEXT_PATH = "beehive.api.context";

	private final RestClient restClient;

	@Inject
	public LocationReportAction(Settings settings, RestController controller, Client client) {
		super(settings, controller, client);
		final String contextPath = settings.get(CONTEXT_ROOT);
		Objects.requireNonNull(contextPath, "Context path should not be null");
		controller.registerHandler(POST, contextPath + "/_search/_report/{location}", this);
		controller.registerHandler(POST, contextPath + "/{index}/_search/_report/{location}", this);
		controller.registerHandler(POST, contextPath + "/{index}/{type}/_search/_report/{location}", this);
		restClient = AccessController.doPrivileged(new PrivilegedAction<RestClient>() {
			@Override
			public RestClient run() {
				return new RestClientBuilder(settings.get(BEEHIVE_API_HOST),
						Integer.valueOf(settings.get(BEEHIVE_API_PORT)), settings.get(BEEHIVE_API_CONTEXT_PATH))
						.setDefaultHeaders(new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer super_token"),
								new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()))
						.build();
			}
		});
	}

	@Override
	protected void handleRequest(RestRequest restRequest, RestChannel restChannel, Client client) throws Exception {
		final List<String> thingIds = AccessController.doPrivileged(new PrivilegedAction<List<String>>() {
			@Override
			public List<String> run() {
				try {
					return Optional.ofNullable(new LocationRequestBuilder(restClient)
							.getThings(restRequest.param("location"), null).build().execute()
							.getJsonResultList(ThingInfo.class)).orElse(Collections.emptyList())
							.stream().map(thingInfo -> thingInfo.getKiiThingID())
							.distinct().collect(Collectors.toList());
				} catch (IOException e) {
					e.printStackTrace();
					return Collections.emptyList();
				}
			}
		});
		final StringBuilder sb = new StringBuilder();
		for (String thingId : thingIds) {
			if (null == thingId || thingId.isEmpty()) {
				continue;
			}
			if (0 != sb.length()) {
				sb.append("\",\"");
			}
			sb.append(thingId);
		}
		final Map<String, Object> params = new HashMap();
		params.put("thingIds", sb.toString());
		SearchRequest searchRequest = new SearchRequest();
		RestSearchAction.parseSearchRequest(searchRequest, restRequest, parseFieldMatcher,
				new SearchSourceBuilder().query(
						QueryBuilders.templateQuery(RestActions.getRestContent(restRequest).toUtf8(), params))
						.buildAsBytes());
		//searchRequest.writeTo(new OutputStreamStreamOutput(System.out));
		client.search(searchRequest, new SourceOnlyListener(restChannel));
	}
}
