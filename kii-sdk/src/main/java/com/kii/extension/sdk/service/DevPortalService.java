package com.kii.extension.sdk.service;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.common.utils.SafeThreadLocal;
import com.kii.extension.sdk.commons.HttpUtils;
import com.kii.extension.sdk.entity.AppDetail;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.AppInfoEntity;
import com.kii.extension.sdk.entity.AppSecret;
import com.kii.extension.sdk.impl.KiiCloudClient;
import com.kii.extension.sdk.impl.PortalApiAccessBuilder;

public class DevPortalService {


	@Autowired
	private KiiCloudClient client;



	private HttpClient httpClient;

	private HttpClientContext context=HttpClientContext.create();

	private CookieStore cookieStore=new BasicCookieStore();

	@Autowired
	private ObjectMapper mapper;

	private String devPortalUrl;





	@PostConstruct
	public void init() throws IOReactorException {

//			context.setCookieStore(cookieStore);
//			RequestConfig globalConfig = RequestConfig.custom()
//					.setCookieSpec(CookieSpecs.DEFAULT)
//					.build();
//			context.setRequestConfig(globalConfig);


			httpClient = HttpClientBuilder.create().setConnectionTimeToLive(120, TimeUnit.SECONDS).build();

	}


	public void setDevPortalUrl(String portalUrl){

		this.devPortalUrl=portalUrl;

	}



	private PortalApiAccessBuilder getBuilder(){
		return new PortalApiAccessBuilder(devPortalUrl);

	}



	private  String executeRequest(HttpUriRequest request){


		HttpResponse response=doRequest(request);

		if(request.getMethod().equals("DELETE")){
			return "";
		}

		return HttpUtils.getResponseBody(response);
	}

	private  HttpResponse doRequest(HttpUriRequest request){


		HttpResponse response= null;
		try {
			response = httpClient.execute(request);


			for(Header h:response.getAllHeaders()){

				System.out.println(h.getName()+":"+h.getValue());
			};
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		return response;

	}

	private Pattern tokenPat=Pattern.compile("(?:authenticity_token)\\\"\\ (?:value)\\=\\\"([^\\\"]*)",Pattern.MULTILINE);

//	private SafeThreadLocal<String> cookieLocal=SafeThreadLocal.getInstance();

	public void login(String user,String pwd){


		try {
			user= URLEncoder.encode(user, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}

		HttpUriRequest loginRequest=getBuilder().buildLoginPrepare().generRequest();


		HttpResponse response=doRequest(loginRequest);

		String loginPage=HttpUtils.getResponseBody(response);

		Header header=response.getFirstHeader("Set-Cookie");

		Matcher match=tokenPat.matcher(loginPage);

		match.find();
		String token=match.group(1);

		HttpUriRequest request=getBuilder().buildLogin(user,pwd,token).generRequest();


		request.setHeader("Cookie",header.getValue());

		HttpResponse respRedir=doRequest(request);

		Header cookHeader=respRedir.getFirstHeader("Set-Cookie");

		CookieHandler handler=new CookieHandler(cookHeader.getValue());

		cookieLocal.set(handler);
		return ;

	}

	private SafeThreadLocal<CookieHandler>  cookieLocal=SafeThreadLocal.getInstance();


	public  static  class CookieHandler{
		private final String cookies;

		private CookieHandler(String str){
			this.cookies=str;
		}
	}



	public AppDetail getAppInfoDetail(String appInfoID){

		HttpUriRequest request= getBuilder().buildAppDetail(appInfoID).generRequest();


		String result= executeRequest(request);


		try {
			return  mapper.readValue(result,AppDetail.class);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}


	}


	public AppSecret getAppInfoSecret(String appInfoID){

		HttpUriRequest request= getBuilder().buildAppSecret(appInfoID).generRequest();


		String result= executeRequest(request);


		try {
			return  mapper.readValue(result,AppSecret.class);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}


	}


	public List<AppInfo>  getAppInfoList(){

		List<AppInfoEntity> list=getAppList();

		List<AppInfo> infoList=new ArrayList<>();
		for(AppInfoEntity  entity:list){

			infoList.add( getAppInfoByEntity( entity));

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				break;
			}
		}

		return infoList;
	}

	private AppInfo getAppInfoByEntity( AppInfoEntity entity) {
		AppDetail detail=getAppInfoDetail(entity.getId());

		AppInfo info=detail.getAppInfo();

		AppSecret secret=getAppInfoSecret(entity.getId());

		secret.fillAppInfo(info);

		return info;
	}

	public AppInfo  getAppInfoByID(String appID){

		List<AppInfoEntity> list=getAppList();

		AppInfoEntity entity=list.stream().filter((info)-> info.getAppID().equals(appID)).findFirst().get();

		return getAppInfoByEntity(entity);
	}

	public List<AppInfoEntity> getAppList(){

		CookieHandler  handler=cookieLocal.get();

		HttpUriRequest request= getBuilder().buildAppList().generRequest();

		request.setHeader("Cookie",handler.cookies);

		String result= executeRequest(request);


		try {
			return  mapper.readValue(result,new TypeReference<List<AppInfoEntity>>(){}  );
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}


}
