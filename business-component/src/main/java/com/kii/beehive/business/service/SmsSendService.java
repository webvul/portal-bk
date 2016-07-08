package com.kii.beehive.business.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.business.manager.SimpleUserManager;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.extension.sdk.commons.HttpTool;

@Component
public class SmsSendService {


	private static final String PREFIX="【蜂巢平台】";

	@Value("${beehive.sms.account.name}")
	private String smsAccountID;

	@Value("${beehive.sms.account.password}")
	private String smsAccountPwd;

	@Value("${beehive.sms.gateway.address}")
	private String smsGatewayAddress;


	@Autowired
	private SimpleUserManager userManager;


	@Autowired
	private ResourceLoader loader;

	@Autowired
	private HttpTool  httpTool;

	@Autowired
	private ShortUrlService  tinyUrlService;

	private String urlTemplate="http://${0}/QxtSms/QxtFirewall?OperID=${1}&OperPass=${2}&SendTime=&ValidTime=&DesMobile=${3}&Content=${4}&ContentType=15";

	private String beehiveUrlTemplate="http://www.openibplatform.com/beehive-portal/api/oauth2/doActivity/${userID}/code/${activityCode}.do";

	public enum SmsType{

		ActivityCode;

	}

	public void sendActivitySms(long userID){


		BeehiveJdbcUser user=userManager.getUserByID(userID);

		String content=getTemplateCtx(SmsType.ActivityCode.name(),user);

		String mobileNumber=user.getPhone();

		verifyMobileNumber(mobileNumber);

		String url= StrTemplate.gener(urlTemplate,smsGatewayAddress,smsAccountID,smsAccountPwd,mobileNumber,content);


		HttpUriRequest request=new HttpGet(url);


		String xml=null;
		try{
			HttpResponse response=httpTool.doRequest(request);
			xml = StreamUtils.copyToString(response.getEntity().getContent(), Charsets.UTF_8);
		}catch(IOException e){
				throw new IllegalArgumentException(e);
		}

		SMSResult  result=getResult(xml);

		if(!result.code.equals("03")){
			throw new IllegalArgumentException("sms send fail");
		}

/*
http://221.179.180.158:9007/QxtSms/QxtFirewall?OperID=test&OperPass=test&SendTime=&ValidTime=&AppendID=1234&DesMobile=13900000000&Content=%D6%D0%CE%C4%B6%CC%D0%C5abc&ContentType=8
 */


	}

	/*
	<?xml version="1.0" encoding="gbk"?>
<response>
<code>03</code>
<message>
	<desmobile>13900000000</desmobile>
	<msgid>200811041234253654785</msgid>
</message>
</response>
	 */

	public static  class SMSResult{

		public String code;

		public String mobile;

		public String msgID;


	}

	private Pattern codeP=Pattern.compile("<code>([^<]+)",Pattern.MULTILINE);
	private Pattern mobileP=Pattern.compile("<desmobile>([^<]+)",Pattern.MULTILINE);
	private Pattern msgP=Pattern.compile("<msgid>([^<]+)",Pattern.MULTILINE);

	public   SMSResult getResult(String xml){




			SMSResult result = new SMSResult();
			Matcher match=codeP.matcher(xml);
			if(match.find()) {
				result.code = match.group(1);
			}
		     match=mobileP.matcher(xml);
			if(match.find()) {
				result.mobile = match.group(1);
			}
			match=msgP.matcher(xml);
			if(match.find()) {
				result.msgID = match.group(1);
			}

			return result;


	}

	private Pattern numPattern=Pattern.compile("^1[\\d]{10}$");

	private void verifyMobileNumber(String mobileNumber){

		if(!numPattern.matcher(mobileNumber).find()){
			 throw new IllegalArgumentException("mobile number invalid");
		};
	}


	private String getTinyUrl(BeehiveJdbcUser  user){


			Map<String,String> paramMap=new HashMap<>();
			paramMap.put("userName",user.getUserName());
			paramMap.put("activityCode",user.getActivityToken());
			paramMap.put("userID",user.getUserID());

			String fullUrl=StrTemplate.generByMap(beehiveUrlTemplate,paramMap);


			try {
					return   tinyUrlService.getShortUrl(fullUrl);
			}catch(IllegalArgumentException e){
					return fullUrl;
			}

	}
	public  String getTemplateCtx(String name, BeehiveJdbcUser  user){


		try {
			String template = StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/smsTemplate/"+name+".template").getInputStream(), Charsets.UTF_8);

			String tinyUrl=getTinyUrl(user);
			Map<String,String> paramMap=new HashMap<>();
			paramMap.put("userName",user.getUserName());
			paramMap.put("activityCode",user.getActivityToken());
			paramMap.put("userID",user.getUserID());
			paramMap.put("url",tinyUrl);

			String smsText=PREFIX+StrTemplate.generByMap(template,paramMap);

			return URLEncoder.encode(smsText, "GBK");

		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("sms template not found or param combine fail:"+name);
		}


	}
}
