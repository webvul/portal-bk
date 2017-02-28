package com.kii.beehive.portal.store.entity.configEntry;

import org.springframework.beans.factory.annotation.Value;

public class EsDataSourceCfgEntry extends BeehiveConfig {

	public static final String ES_DS_CONFIG = "esDSConfig";
	@Value("${elasticsearch.business.common.field.carId}")
	private String bizDataCommonCarId;
	@Value("${elasticsearch.business.common.field.eventTime}")
	private String bizDataCommonEventTime;
	@Value("${elasticsearch.business.parkingspace.index}")
	private String bizDataParkingSpaceIndex;
	@Value("${elasticsearch.business.parkingspace.indexType.leave}")
	private String bizDataParkingSpaceIndexTypeLeave;
	@Value("${elasticsearch.business.gateway.index}")
	private String bizDataGatewayIndex;
	@Value("${elasticsearch.business.gateway.indexType.leave}")
	private String bizDataGatewayIndexTypeLeave;
	
	public EsDataSourceCfgEntry() {
		
		super.setConfigName(ES_DS_CONFIG);
	}

	public String getBizDataCommonCarId() {
		return bizDataCommonCarId;
	}

	public void setBizDataCommonCarId(String bizDataCommonCarId) {
		this.bizDataCommonCarId = bizDataCommonCarId;
	}

	public String getBizDataCommonEventTime() {
		return bizDataCommonEventTime;
	}

	public void setBizDataCommonEventTime(String bizDataCommonEventTime) {
		this.bizDataCommonEventTime = bizDataCommonEventTime;
	}

	public String getBizDataParkingSpaceIndex() {
		return bizDataParkingSpaceIndex;
	}

	public void setBizDataParkingSpaceIndex(String bizDataParkingSpaceIndex) {
		this.bizDataParkingSpaceIndex = bizDataParkingSpaceIndex;
	}

	public String getBizDataParkingSpaceIndexTypeLeave() {
		return bizDataParkingSpaceIndexTypeLeave;
	}

	public void setBizDataParkingSpaceIndexTypeLeave(String bizDataParkingSpaceIndexTypeLeave) {
		this.bizDataParkingSpaceIndexTypeLeave = bizDataParkingSpaceIndexTypeLeave;
	}

	public String getBizDataGatewayIndex() {
		return bizDataGatewayIndex;
	}

	public void setBizDataGatewayIndex(String bizDataGatewayIndex) {
		this.bizDataGatewayIndex = bizDataGatewayIndex;
	}

	public String getBizDataGatewayIndexTypeLeave() {
		return bizDataGatewayIndexTypeLeave;
	}

	public void setBizDataGatewayIndexTypeLeave(String bizDataGatewayIndexTypeLeave) {
		this.bizDataGatewayIndexTypeLeave = bizDataGatewayIndexTypeLeave;
	}



}
