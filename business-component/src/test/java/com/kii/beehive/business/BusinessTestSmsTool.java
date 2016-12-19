package com.kii.beehive.business;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.manager.SimpleUserManager;
import com.kii.beehive.business.service.sms.SmsSendService;
import com.kii.beehive.business.service.sms.SmsSendTool;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;

public class BusinessTestSmsTool extends BusinessTestTemplate {

	@Autowired
	private SmsSendService  service;


	@Autowired
	private SimpleUserManager userManager;

	@Test
	public void testSms(){

		service.sendActivitySms(userManager.getUserByID(444l),"token");

	}

	@Test
	public void testResetPwd(){

		service.sendResetPwdSms("0e14db00-18d4-11e6-9c6d-00163e007aba","token");

	}


	@Test
	public void testResult(){

		String xml="<?xml version=\"1.0\" encoding=\"gbk\"?>\n" +
				"<response>\n" +
				"<code>03</code>\n" +
				"<message>\n" +
				"\t<desmobile>13900000000</desmobile>\n" +
				"\t<msgid>200811041234253654785</msgid>\n" +
				"</message>\n" +
				"</response>";



		SmsSendTool.SMSResult result=new SmsSendTool.SMSResult(xml);

		assertEquals("03",result.code);
		assertEquals("13900000000",result.mobile);
		assertEquals("200811041234253654785",result.msgID);


		String xmlErr="<?xml version=\"1.0\" encoding=\"gbk\"?>\n" +
				"<response>\n" +
				"<code>05</code>\n" +
				"</response>";


		result=new SmsSendTool.SMSResult(xmlErr);

		assertEquals("05",result.code);

	}

	@Test
	public void testShortUrl(){



	}
	@Test
	public void testTemplate() throws UnsupportedEncodingException {

		BeehiveJdbcUser user=new BeehiveJdbcUser();
		user.setPhone("1300000000");
		user.setUserID("ababab");
		user.setUserName("张三");
		user.setActivityToken("qwerty");

		String fullCtx=service.getTemplateCtx(SmsSendService.SmsType.activityCode.name(),user,"token");

		String decodeCtx= URLDecoder.decode(fullCtx,"GBK");

		System.out.println(decodeCtx);

	}
}
