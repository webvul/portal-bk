package com.kii.beehive.business.service;

import java.io.IOException;
import java.net.URLEncoder;
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

	private String urlTemplate="http://${0}/QxtSms/QxtFirewall?OperID=${1}&OperPass=${2}&SendTime=&ValidTime=&DesMobile=${3}&Content=${4}$ContentType=15";

	public enum SmsType{

		ActivityCode;

	}

	public void sendSmsToUser(long userID,SmsType  type){


		BeehiveJdbcUser user=userManager.getUserByID(userID);

		String content=getTemplateCtx(type.name(),user);

		String mobileNumber=user.getPhone();

		verifyMobileNumber(mobileNumber);

		String fullUrl= StrTemplate.gener(urlTemplate,smsGatewayAddress,smsAccountID,smsAccountPwd,mobileNumber,content);


		HttpUriRequest request=new HttpGet(fullUrl);

		HttpResponse response=httpTool.doRequest(request);



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

	private Pattern numPattern=Pattern.compile("^1[\\d]{10}$");

	private void verifyMobileNumber(String mobileNumber){

		if(!numPattern.matcher(mobileNumber).find()){
			 throw new IllegalArgumentException("mobile number invalid");
		};
	}

	private String getTemplateCtx(String name, BeehiveJdbcUser  user){


		try {
			String template = StreamUtils.copyToString(loader.getResource("classpath::com/kii/beehive/business/smsTemplate/"+name+".template").getInputStream(), Charsets.UTF_8);
			String smsText=StrTemplate.generByEntity(template,user);

			return URLEncoder.encode(smsText, "GBK");

		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("sms template not found or param combine fail:"+name);
		}


	}
}
