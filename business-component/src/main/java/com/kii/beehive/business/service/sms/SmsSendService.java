package com.kii.beehive.business.service.sms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.business.manager.SimpleUserManager;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;

@Component
public class SmsSendService {

	private Logger log= org.slf4j.LoggerFactory.getLogger(SmsSendService.class);



	@Autowired
	private SimpleUserManager  userManager;

	@Autowired
	private ResourceLoader loader;


	@Autowired
	private ShortUrlService  tinyUrlService;

	@Autowired
	private SmsSendTool  smsTool;

	private String beehiveUrlTemplate="http://www.openibplatform.com/beehive-portal/api/oauth2/doActivity/${userID}/code/${activityCode}.do";

	public enum SmsType{

		activityCode,resetPwd;
	}


	public void sendResetPwdSms(String userID,String token){

		BeehiveJdbcUser  user=userManager.getUserByUserID(userID);

		String content=getTemplateCtx(SmsType.resetPwd.name(),user,token);

		String mobileNumber=user.getPhone();


		smsTool.sendSms(content, mobileNumber);

/*
http://221.179.180.158:9007/QxtSms/QxtFirewall?OperID=test&OperPass=test&SendTime=&ValidTime=&AppendID=1234&DesMobile=13900000000&Content=%D6%D0%CE%C4%B6%CC%D0%C5abc&ContentType=8
 */


	}


	public void sendActivitySms(BeehiveJdbcUser user,String token){


		String content=getTemplateCtx(SmsType.activityCode.name(),user,token);

		String mobileNumber=user.getPhone();


		smsTool.sendSms(content, mobileNumber);

/*
http://221.179.180.158:9007/QxtSms/QxtFirewall?OperID=test&OperPass=test&SendTime=&ValidTime=&AppendID=1234&DesMobile=13900000000&Content=%D6%D0%CE%C4%B6%CC%D0%C5abc&ContentType=8
 */


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
	public  String getTemplateCtx(String name, BeehiveJdbcUser  user,String token){


		try {
			String template = StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/smsTemplate/"+name+".template").getInputStream(), Charsets.UTF_8);

//			String tinyUrl=getTinyUrl(user);
			Map<String,String> paramMap=new HashMap<>();
			paramMap.put("userName",user.getUserName());
			paramMap.put("activityCode",token);
			paramMap.put("userID",user.getUserID());
//			paramMap.put("url",tinyUrl);

			return StrTemplate.generByMap(template,paramMap);

		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("sms template not found or param combine fail:"+name);
		}


	}
}
