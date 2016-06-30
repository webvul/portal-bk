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

	private final String DATE_FIELD = "state.taiwanNo1";
	private final String TERM_FIELD = "target";

	public String search(GlobalThingInfo thing, String queryString) {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		//HttpClient
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

		HttpPost httpPost = new HttpPost(elasticsearchHost + thing.getKiiAppID() + "/spark/_search");
		httpPost.setHeader("Authorization", "Bearer super_token");

		try {
			StringEntity params = new StringEntity(queryString);
			httpPost.setEntity(params);

			HttpResponse httpResponse = closeableHttpClient.execute(httpPost);

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				return EntityUtils.toString(entity);
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

	public String extractResultForAggs(String result) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return actualObj.get("aggregations").get("time_buckets").toString();
	}

	public String extractResultForHistorical(String result) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return actualObj.get("hits").toString();
	}

	public String queryBuilderForAggs(String kiiThingID, long startDate, long endDate, String intervalField, String
			operatorField, String[] avgFields) {

		StringBuilder avgFieldSb = new StringBuilder();
		for (String avgField : avgFields) {
			if (avgFieldSb.length() > 0)
				avgFieldSb.append(",");
			avgFieldSb.append("\"").append(avgField).append("\" : {\"").append(operatorField).append("\": { \"field\": \"").append(avgField).append("\"}}");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("{\"query\" : {\"filtered\" : {")
				.append("\"query\":{\"term\": {\"").append(TERM_FIELD).append("\":\"").append(kiiThingID).append("\"}},")
				.append("\"filter\": {\"range\": {\"").append(DATE_FIELD).append("\": {\"gte\":").append(startDate).append(",\"lte\":").append(endDate).append("}}}}")
				.append("},");
		sb.append("\"size\": 0,");
		sb.append("\"sort\": [{ \"").append(DATE_FIELD).append("\": { \"order\": \"desc\" }}],");
		sb.append("\"aggs\" : {\"time_buckets\" : {\"date_histogram\" : { \"field\" : \"").append(DATE_FIELD).append("\",\"interval\" : \"").append(intervalField).append("\"},")
				.append("\"aggs\" : {")
				.append(avgFieldSb)
				.append("}}}");
		sb.append("}");
		return sb.toString();
	}

	public String queryBuilderForHistorical(String kiiThingID, long startDate, long endDate, int size, int from) {

		StringBuilder sb = new StringBuilder();
		sb.append("{\"query\" : {\"filtered\" : {")
				.append("\"query\":{\"term\": {\"").append(TERM_FIELD).append("\":\"").append(kiiThingID).append("\"}},")
				.append("\"filter\": {\"range\": {\"").append(DATE_FIELD).append("\": {\"gte\":").append(startDate).append(",\"lte\":").append(endDate).append("}}}}")
				.append("},");
		sb.append("\"size\": ").append(size).append(",");
		sb.append("\"from\": ").append(from).append(",");
		sb.append("\"sort\": [{ \"").append(DATE_FIELD).append("\": { \"order\": \"desc\" }}]");
		sb.append("}");
		return sb.toString();
	}
}
