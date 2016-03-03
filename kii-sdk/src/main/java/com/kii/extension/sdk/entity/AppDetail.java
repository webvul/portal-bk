package com.kii.extension.sdk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppDetail {

	private String id;

	private String name;

	private String appID;

	private String appKey;


	private SiteType site;


	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	public String getAppID() {
		return appID;
	}

	@JsonProperty("app_id")
	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getAppKey() {
		return appKey;
	}

	@JsonProperty("app_key")
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}



	public SiteType getSite() {
		return site;
	}

	@JsonProperty("site_name")
	public void setSite(String  site) {
		this.site = SiteType.valueOf(site.toUpperCase());
	}

	public AppInfo getAppInfo() {

		AppInfo info=new AppInfo();
		info.setAppID(appID);
		info.setAppKey(appKey);
		info.setName(name);
		info.setSiteType(site);

		return info;
	}

	 /*

	{"id":17796,"name":"Yankon_CN3","description":"",
	"urls":{"facebook":"","app_store":"","chrome_store":"","android_market":""},
	"preferences":null,"icon_uid":null,"app_id":"d93defd9","app_key":"4e96ba956153ef6c68c512ed0900f787","platforms":["android","ios"],
	"state":null,"user_id":13770,"deleted_at":null,"created_at":"2015-04-17T03:46:42.180Z","updated_at":"2015-09-09T08:54:48.518Z",
	"client_id":"1234567890123456789012345678901234567890","client_secret":"9876543219876543219876543219876543219876543219876543219876543219",
	"ads_enabled":null,"ads_settings":"--- {}\n","country":"cn3",
	"configuration":{"isMasterApp":"false",
	"emailAddress":"d93defd9@kii.com","facebookAppID":"","twitterConsumerID":"","passwordResetMethod":"GENERATE_PASSWORD","refreshTokenEnabled":"false","twitterConsumerSecret":"","sendReferralForLongAPNS":"false","verificationSmsTemplate":"","reservedFieldsValidation":"true","maxTokenExpirationSeconds":"2147483640","exposeFullUserDataToOthers":"false","passwordResetTimeoutSeconds":"604800","defaultTokenExpirationSeconds":"2147483640","gcmCollapseKeyDefaultBehavior":"SEND_BUCKET_ID","passwordResetOKRedirectionURL":"","phoneNumberVerificationRequired":"false","emailAddressVerificationRequired":"false","isThingTypeConfigurationRequired":"false","emailVerificationOKRedirectionURL":"","passwordResetFailureRedirectionURL":"","emailVerificationFailureRedirectionURL":""}
	,"gcm_key":null,"apns_configuration":{},"email_from_address":null,"sent_message_count":1,"draft_message_count":0,"zuora_subscription_id":"","kii_cloud_plan_id":"","kii_fa_packs":"","plan_id":null,
	"usage":
	"{\"free_plan\":{\"Kii_cloud_datasize\":0.0018900921568274498,\"Kii_cloud_push_sent\":3.669,\"Kii_cloud_api_call\":95.438}}","addon_ids":null,"downgrades":null,"jpush_keys":{},"inactive":false,"inactivated_at":null,
	"activities":
	"[{\"time\":1441174930,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441174931,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441175171,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441175175,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441175479,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"app_yankon@kii.com\"},
	{\"time\":1441536355,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"116.231.221.235\",\"by\":\"app_yankon@kii.com\"},
	{\"time\":1441539167,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"116.231.221.235\",\"by\":\"app_yankon@kii.com\"},
	{\"time\":1441542622,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"116.231.221.235\",\"by\":\"app_yankon@kii.com\"},
	{\"time\":1441778044,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441783982,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"116.231.221.235\",\"by\":\"app_yankon@kii.com\"},
	{\"time\":1441785926,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"116.231.221.235\",\"by\":\"app_yankon@kii.com\"},
	{\"time\":1441785932,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"116.231.221.235\",\"by\":\"app_yankon@kii.com\"},
	{\"time\":1441786054,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441786634,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441786634,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441786642,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441787835,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441787835,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441787842,\"type\":\"ADMIN_TOKEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"},
	{\"time\":1441788888,\"type\":\"OPEN\",\"ipaddr\":\"107.158.236.114\",\"by\":\"steven.jiang@kii.com\"}]"
	,"site_name":"cn3","supported_bucket_types":["READ_WRITE"],"removed":false,"removed_at":null,"app_name":"YankonCn3",
	"app_sites":{"ios":"kiiSiteCN3","html5":"KiiSite.CN3","android":"Site.CN3","unity":"Kii.Site.CN3"}
	,"addon_product_ids":[],
	"stats":[
	{"id":82626,"title":"Kii_dm_flex_created","name":"","app_id":17796,"position":null,"remote_id":"1112511","application_id":null,"remote_metric_name":"Kii_dm_flex_created","data_provider":null,"transaction":"{\"metric\":{\"id\":1112511,\"name\":\"Kii_dm_flex_created\",\"system_name\":\"Kii_dm_flex_created\",\"unit\":\"objects\"},\"period\":{\"name\":null,\"since\":\"2015-04-01T00:00:00Z\",\"until\":\"2015-09-30T23:59:59Z\",\"granularity\":\"month\"},\"total\":73360,\"values\":[4107,30769,2925,9183,4939,21437],\"application\":{\"id\":1409611801555,\"name\":\"Yankon_CN3\",\"state\":\"live\",\"description\":\"empty\",\"plan\":{\"id\":86066,\"name\":\"Limited plan\"},\"account\":{\"id\":2445581248372,\"name\":\"N/A\"}}}","created_at":"2015-04-17T03:47:33.203Z","updated_at":"2015-09-09T08:37:16.795Z"},
	{"id":83334,"title":"Files created (bytes)","name":"","app_id":17796,"position":null,"remote_id":"1112412","application_id":null,"remote_metric_name":"Kii_dm_file_increased_bytes","data_provider":null,"transaction":"{\"metric\":{\"id\":1112412,\"name\":\"Files created (bytes)\",\"system_name\":\"Kii_dm_file_increased_bytes\",\"unit\":\"bytes\"},\"period\":{\"name\":null,\"since\":\"2015-04-01T00:00:00Z\",\"until\":\"2015-09-30T23:59:59Z\",\"granularity\":\"month\"},\"total\":2738481,\"values\":[1318,2267543,469620,0,0,0],\"application\":{\"id\":1409611801555,\"name\":\"Yankon_CN3\",\"state\":\"live\",\"description\":\"empty\",\"plan\":{\"id\":86066,\"name\":\"Limited plan\"},\"account\":{\"id\":2445581248372,\"name\":\"N/A\"}}}","created_at":"2015-04-22T06:30:06.935Z","updated_at":"2015-09-02T06:22:15.774Z"},
	{"id":83333,"title":"Files created","name":"","app_id":17796,"position":null,"remote_id":"1112432","application_id":null,"remote_metric_name":"Kii_dm_file_increased_count","data_provider":null,"transaction":"{\"metric\":{\"id\":1112432,\"name\":\"Files created\",\"system_name\":\"Kii_dm_file_increased_count\",\"unit\":\"files\"},\"period\":{\"name\":null,\"since\":\"2015-04-01T00:00:00Z\",\"until\":\"2015-09-30T23:59:59Z\",\"granularity\":\"month\"},\"total\":10,\"values\":[2,6,2,0,0,0],\"application\":{\"id\":1409611801555,\"name\":\"Yankon_CN3\",\"state\":\"live\",\"description\":\"empty\",\"plan\":{\"id\":86066,\"name\":\"Limited plan\"},\"account\":{\"id\":2445581248372,\"name\":\"N/A\"}}}","created_at":"2015-04-22T06:30:06.928Z","updated_at":"2015-09-02T06:22:15.559Z"},
	{"id":82625,"title":"Kii_um_created","name":"","app_id":17796,"position":null,"remote_id":"1112491","application_id":null,"remote_metric_name":"Kii_um_created","data_provider":null,"transaction":"{\"metric\":{\"id\":1112491,\"name\":\"Kii_um_created\",\"system_name\":\"Kii_um_created\",\"unit\":\"users\"},\"period\":{\"name\":null,\"since\":\"2015-04-01T00:00:00Z\",\"until\":\"2015-09-30T23:59:59Z\",\"granularity\":\"month\"},\"total\":65,\"values\":[7,13,9,10,17,9],\"application\":{\"id\":1409611801555,\"name\":\"Yankon_CN3\",\"state\":\"live\",\"description\":\"empty\",\"plan\":{\"id\":86066,\"name\":\"Limited plan\"},\"account\":{\"id\":2445581248372,\"name\":\"N/A\"}}}","created_at":"2015-04-17T03:47:32.095Z","updated_at":"2015-09-09T07:32:55.583Z"}]
	,"conversion_rules":[],"created":"2015-04-17T03:46:42.180Z","owner":false,"kiicloud_endpoint":"https://api-cn3.kii.com/api","acl":{"deny":["X_CHANGE_PLAN","X_ADD_COLLABORATOR"],"allow":[]},"plan":{"id":null,"type":"free","limits":"{\"Kii_cloud_datasize\":{\"name\":\"Kii Cloud Datasize\",\"units\":1,\"overage\":0.2,\"unit_multiplier\":\"1\"},\n      \"Kii_cloud_push_sent\":{\"name\":\"Kii Cloud Push Notifications\",\"units\":1000,\"overage\":0.07,\"unit_multiplier\":\"1000\"},\n      \"Kii_cloud_api_call\":{\"name\":\"Kii Cloud Api Call\",\"units\":1000,\"overage\":0.07,\"unit_multiplier\":\"1000\"}}","monthly":0,"pending":false}}
	 */
}
