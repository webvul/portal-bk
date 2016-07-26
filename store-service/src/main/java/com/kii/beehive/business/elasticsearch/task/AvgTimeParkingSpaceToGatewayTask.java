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
	private String parkingSpaceIndex;

	private String gatewayIndex;

	private String carIdField;

	private String eventTimeField;

	private double averageTime;

	public double getAverageTime() {
		return averageTime;
	}

	public void setParkingSpaceIndex(String parkingSpaceIndex) {
		this.parkingSpaceIndex = parkingSpaceIndex;
	}

	public void setGatewayIndex(String gatewayIndex) {
		this.gatewayIndex = gatewayIndex;
	}

	public void setCarIdField(String carIdField) {
		this.carIdField = carIdField;
	}

	public void setEventTimeField(String timestampField) {
		this.eventTimeField = timestampField;
	}

	@Override
	protected SearchResponse processRequest(Client client) {
		SearchResponse response = super.processRequest(client);

		StringTerms result = response.getAggregations().get(carIdField);
		int count = 0;
		long total = 0;
		for (Terms.Bucket bucket : result.getBuckets()) {
			SearchHit[] topHits = ((InternalTopHits) bucket.getAggregations().asList().get(0)).getHits().getHits();
			int i = 0;
			while (i < topHits.length) {
				long start = -1;
				long end = -1;
				while (i < topHits.length - 1) {
					if (topHits[i].getIndex().equals(gatewayIndex)
							&& topHits[i + 1].getIndex().equals(parkingSpaceIndex)) {
						start = Long.valueOf(topHits[i].getSortValues()[0].toString());
						break;
					}
					i++;
				}
				while (++i < topHits.length) {
					if (topHits[i].getIndex().equals(parkingSpaceIndex)) {
						end = Long.valueOf(topHits[i].getSortValues()[0].toString());
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
