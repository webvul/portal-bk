package com.kii.beehive.business.elasticsearch.task;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;

/**
 * Created by hdchen on 7/25/16.
 */
public class AvgTimeParkingSpaceToGatewayTask extends SearchTask {
	private String parkingSpaceType;

	private String gatewayType;

	private String carIdField;

	private String timestampField;

	private double averageTime;

	public double getAverageTime() {
		return averageTime;
	}

	public void setParkingSpaceType(String parkingSpaceType) {
		this.parkingSpaceType = parkingSpaceType;
	}

	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}

	public void setCarIdField(String carIdField) {
		this.carIdField = carIdField;
	}

	public void setTimestampField(String timestampField) {
		this.timestampField = timestampField;
	}

	@Override
	protected SearchResponse processRequest(Client client) {
		SearchResponse response = super.processRequest(client);

		StringTerms result = response.getAggregations().get(carIdField);
		int count = 0;
		int total = 0;
		for (Terms.Bucket bucket : result.getBuckets()) {
			SearchHit[] topHits = ((InternalTopHits) bucket.getAggregations().asList().get(0)).getHits().getHits();
			int i = 0;
			while (i < topHits.length) {
				long start = -1;
				long end = -1;
				while (i < topHits.length - 1) {
					if (topHits[i].getType().equals(gatewayType)
							&& topHits[i + 1].getType().equals(parkingSpaceType)) {
						start = (long) topHits[i].getSortValues()[0];
						break;
					}
					i++;
				}
				while (++i < topHits.length) {
					if (topHits[i].getType().equals(parkingSpaceType)) {
						end = (long) topHits[i].getSortValues()[0];
						break;
					}
				}
				if (-1 != start && -1 != end && start >= end) {
					count++;
					total += start - end;
				}
			}
		}

		if (0 < count) {
			averageTime = (double) total / count;
		}
		return response;
	}
}
