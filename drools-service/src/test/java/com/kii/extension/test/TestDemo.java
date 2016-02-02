package com.kii.extension.test;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.ruleengine.DemoRuleLoader;
import com.kii.extension.ruleengine.demo.Message;

public class TestDemo extends InitTest {

	@Autowired
	private DemoRuleLoader  ruleLoader;


	@Test
	public void testFire() throws IOException {

		// go !
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);

		ruleLoader.addData(message);

		ruleLoader.fireCondition();

		System.in.read();

	}
}
