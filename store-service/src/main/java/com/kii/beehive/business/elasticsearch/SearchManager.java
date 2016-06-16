package com.kii.beehive.business.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Jasonhang on 15/6/2016.
 */
@Component
public class SearchManager {

	@Value("${beehive.portal.elasticsearch.host}")
	private String elasticsearchHost;

	public String search(GlobalThingInfo thing, String queryString) {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		//HttpClient
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

		HttpPost httpPost = new HttpPost(elasticsearchHost + thing.getKiiAppID() + "/_search");

		try {
			StringEntity params = new StringEntity(queryString);
			httpPost.setEntity(params);

			HttpResponse httpResponse = closeableHttpClient.execute(httpPost);

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode actualObj = mapper.readTree(EntityUtils.toString(entity));
				return actualObj.get("aggregations").get("time_buckets").toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public String queryBuilder(String venderThingID, long startDate, long endDate, String intervalField, String
			operatorField, String[] avgFields) {
		String dateField = "_modified";
		String termField = "target";

		StringBuilder avgFieldSb = new StringBuilder();
		for (String avgField : avgFields) {
			if (avgFieldSb.length() > 0)
				avgFieldSb.append(",");
			avgFieldSb.append("\"").append(avgField).append("\" : {\"").append(operatorField).append("\": { \"field\": \"state.actionResults.").append(avgField).append("\"}}");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("{\"query\" : {\"filtered\" : {")
				.append("\"query\":{\"term\": {\"").append(termField).append("\":\"").append(venderThingID).append("\"}},")
				.append("\"filter\": {\"range\": {\"").append(dateField).append("\": {\"gte\":").append(startDate).append(",\"lte\":").append(endDate).append("}}}}")
				.append("},");
		sb.append("\"size\": 0,");
		sb.append("\"sort\": [{ \"").append(dateField).append("\": { \"order\": \"desc\" }}],");
		sb.append("\"aggs\" : {\"time_buckets\" : {\"date_histogram\" : { \"field\" : \"").append(dateField).append("\",\"interval\" : \"").append(intervalField).append("\"},")
				.append("\"aggs\" : {")
				.append(avgFieldSb)
				.append("}}}");
		sb.append("}");
		return sb.toString();
	}
}
