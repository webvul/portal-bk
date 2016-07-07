package com.kii.beehive.business;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.service.SmsSendService;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;

public class TestSmsTool extends TestInit {

	@Autowired
	private SmsSendService  service;


	@Test
	public void testSms(){

		service.sendSmsToUser(444l, SmsSendService.SmsType.ActivityCode,"www.beehive.com.cn/portal/");

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



		SmsSendService.SMSResult result=service.getResult(xml);

		assertEquals("03",result.code);
		assertEquals("13900000000",result.mobile);
		assertEquals("200811041234253654785",result.msgID);


		String xmlErr="<?xml version=\"1.0\" encoding=\"gbk\"?>\n" +
				"<response>\n" +
				"<code>05</code>\n" +
				"</response>";


		result=service.getResult(xmlErr);

		assertEquals("05",result.code);

	}
	@Test
	public void testTemplate() throws UnsupportedEncodingException {

		BeehiveJdbcUser user=new BeehiveJdbcUser();
		user.setPhone("1300000000");
		user.setUserID("ababab");
		user.setUserName("张三");
		user.setActivityToken("qwerty");

		String fullCtx=service.getTemplateCtx(SmsSendService.SmsType.ActivityCode.name(),user,"localhost");

		String decodeCtx= URLDecoder.decode(fullCtx,"GBK");

		System.out.println(decodeCtx);

	}
}
