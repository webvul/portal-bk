package com.kii.beehive.business.service.sms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.extension.sdk.commons.HttpTool;

@Component
public class SmsSendTool {

	private Logger log= org.slf4j.LoggerFactory.getLogger(SmsSendTool.class);


	@Autowired
	private HttpTool httpTool;


	private static final String PREFIX="【蜂巢平台】";

	@Value("${beehive.sms.account.name}")
	private String smsAccountID;

	@Value("${beehive.sms.account.password}")
	private String smsAccountPwd;

	@Value("${beehive.sms.gateway.address}")
	private String smsGatewayAddress;

	private static String urlTemplate="http://${0}/QxtSms/QxtFirewall?OperID=${1}&OperPass=${2}&SendTime=&ValidTime=&DesMobile=${3}&Content=${4}&ContentType=15";




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

		private static Pattern codeP=Pattern.compile("<code>([^<]+)",Pattern.MULTILINE);
		private static Pattern mobileP=Pattern.compile("<desmobile>([^<]+)",Pattern.MULTILINE);
		private static Pattern msgP=Pattern.compile("<msgid>([^<]+)",Pattern.MULTILINE);

		private SMSResult(){
			code="99";
		}

		public  SMSResult(String xml) {
			Matcher match = codeP.matcher(xml);
			if (match.find()) {
				code = match.group(1);
			}
			match = mobileP.matcher(xml);
			if (match.find()) {
				mobile = match.group(1);
			}
			match = msgP.matcher(xml);
			if (match.find()) {
				msgID = match.group(1);
			}
		}


		public String code;

		public String mobile;

		public String msgID;


	}


	@Async
	public SMSResult sendSms(String content, String mobileNumber) {


		if(!verifyMobileNumber(mobileNumber)){

			log.error("invalie mobile number,sms cannot sended");
			return new SMSResult();
		};


		String fullContent= null;
		try {
			fullContent = URLEncoder.encode(content+PREFIX, "GBK");
		} catch (UnsupportedEncodingException e) {
		}


		

		String url= StrTemplate.gener(urlTemplate,smsGatewayAddress,smsAccountID,smsAccountPwd,mobileNumber,fullContent);


		HttpUriRequest request=new HttpGet(url);


		String xml=null;
		try{
			HttpResponse response=httpTool.doRequest(request);
			xml = StreamUtils.copyToString(response.getEntity().getContent(), Charsets.UTF_8);
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}

		SMSResult result=new SMSResult(xml);

		if(!result.code.equals("03")){
			throw new IllegalArgumentException("sms send fail");
		}

		return result;
	}


	private Pattern numPattern=Pattern.compile("^1[\\d]{10}$");

	private boolean verifyMobileNumber(String mobileNumber){

		if(StringUtils.isEmpty(mobileNumber)){
			return false;
		}
		return numPattern.matcher(mobileNumber).find();
	}

}
