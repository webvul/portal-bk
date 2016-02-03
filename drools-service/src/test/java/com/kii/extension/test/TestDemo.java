package com.kii.extension.test;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.drools.core.common.DefaultFactHandle;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.kii.extension.ruleengine.DemoRuleLoader;
import com.kii.extension.ruleengine.demo.Message;

public class TestDemo extends InitTest {

	private Logger log= LoggerFactory.getLogger(TestDemo.class);


	@Autowired
	private DemoRuleLoader  ruleLoader;

	@Autowired
	private ResourceLoader  loader;

	@Before
	public void init() throws IOException {


		String drl= StreamUtils.copyToString(loader.getResource("classpath:com/kii/extension/ruleengine/demo.drl").getInputStream(), StandardCharsets.UTF_8);

		ruleLoader.initCondition(drl);
	}


	@Test
	public void testFire() throws IOException {

		// go !
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);

		DefaultFactHandle  handle= (DefaultFactHandle) ruleLoader.addData(message);

		ruleLoader.fireCondition();

		Message msg= (Message) handle.getObject();

		assertEquals(msg.getStatus(),1);

//		System.in.read();



	}
}
