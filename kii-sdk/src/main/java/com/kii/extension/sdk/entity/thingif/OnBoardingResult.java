package com.kii.extension.sdk.entity.thingif;

public class OnBoardingResult {

	/*
	   "thingID":"th.aaaa",
   "accessToken":"xxxxxx",

	 */

	private String thingID;

	private  String accessToken;

	private MqttEndPoint  mqttEndpoint;

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public MqttEndPoint getMqttEndpoint() {
		return mqttEndpoint;
	}

	public void setMqttEndpoint(MqttEndPoint mqttEndpoint) {
		this.mqttEndpoint = mqttEndpoint;
	}
}
