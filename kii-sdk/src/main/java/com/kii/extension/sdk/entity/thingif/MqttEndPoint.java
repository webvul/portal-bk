package com.kii.extension.sdk.entity.thingif;

public class MqttEndPoint {
	/*
	"mqttEndpoint":{
		"installationID":"4qxjayegngnfcq3f8sw7d9l0e",
				"host":"kii.com",
				"mqttTopic","testtopic",
				"userName":"testuser",
				"password":"testpassword",
				"portSSL":445,
				"portTCP":85,
				"ttl":2147483647
	}*/

	private String installationID;

	private String host;

	private String mqttTopic;

	private String userName;

	private String password;

	private int portSSL;

	private int portTCP;

	private int ttl;

	public String getInstallationID() {
		return installationID;
	}

	public void setInstallationID(String installationID) {
		this.installationID = installationID;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getMqttTopic() {
		return mqttTopic;
	}

	public void setMqttTopic(String mqttTopic) {
		this.mqttTopic = mqttTopic;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPortSSL() {
		return portSSL;
	}

	public void setPortSSL(int portSSL) {
		this.portSSL = portSSL;
	}

	public int getPortTCP() {
		return portTCP;
	}

	public void setPortTCP(int portTCP) {
		this.portTCP = portTCP;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
}
