package com.kii.extension.sdk.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AppInfoEntity implements Serializable{


	/*
	[{"id":8436,"app_id":"aa407bbe","name":"platformStore","country":"us","platforms":["html5"],"created":"2014-07-09T02:06:52.817Z","inactive":false,"owner":true},
	{"id":10157,"app_id":"2051e8e1","name":"Kii Tutorial","country":"us","platforms":["android","ios"],"created":"2014-09-01T09:37:37.470Z","inactive":false,"owner":true},
	{"id":13515,"app_id":"66221583","name":"LEDbackend","country":"us","platforms":["android","ios"],"created":"2014-12-01T07:07:18.475Z","inactive":false,"owner":true},
	{"id":16661,"app_id":"531a4df7","name":"yankon-aliyun","country":"cn3","platforms":["android","ios"],"created":"2015-03-10T06:57:44.598Z","inactive":false,"owner":true},
	{"id":17796,"app_id":"d93defd9","name":"Yankon_CN3","country":"cn3","platforms":["android","ios"],"created":"2015-04-17T03:46:42.180Z","inactive":false,"owner":false},
	{"id":17363,"app_id":"89ba7c08","name":"YouWill","country":"cn","platforms":["android","ios","html5"],"created":"2015-04-02T02:07:08.524Z","inactive":false,"owner":false},
	{"id":17787,"app_id":"5318608a","name":"Yankon_US","country":"us","platforms":["android","ios"],"created":"2015-04-17T01:46:47.939Z","inactive":false,"owner":false},
	{"id":8051,"app_id":"fae4ad0e","name":"Rooti CliMate","country":"us","platforms":["android","ios","html5"],"created":"2014-06-25T05:47:24.550Z","inactive":false,"owner":false},
	{"id":8393,"app_id":"d7f7fb4d","name":"WMe","country":"us","platforms":["android","ios","html5"],"created":"2014-07-07T13:31:57.062Z","inactive":false,"owner":false},
	{"id":10141,"app_id":"c99e04f1","name":"YouWill AppStore Project","country":"cn","platforms":["android","html5"],"created":"2014-09-01T03:27:34.351Z","inactive":false,"owner":false},
	{"id":14136,"app_id":"06e806e2","name":"Yankon Project","country":"jp","platforms":["android","ios","html5"],"created":"2014-12-19T03:13:21.089Z","inactive":false,"owner":false}]
	 */



	private String id;

	private String name;

	private boolean owner;

	private boolean inAction;

	private Date createTime;

	private String appID;

	private SiteType site;

	public AppInfoEntity(){

	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("owner")
	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner){
		this.owner=owner;
	}

	@JsonProperty("inaction")
	public boolean isInAction() {
		return inAction;
	}

	public void setInAction(boolean inaction){
		this.inAction=inaction;
	}


	public Date getCreateTime() {
		return createTime;
	}

	@JsonProperty("created")
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@JsonProperty("app_id")
	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}


	public SiteType getSite() {
		return site;
	}

	@JsonProperty("country")
	public void setSite(String site) {
		this.site = SiteType.valueOf(site.toUpperCase());
	}
}
