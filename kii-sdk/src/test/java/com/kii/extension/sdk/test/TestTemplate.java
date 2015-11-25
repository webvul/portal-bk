package com.kii.extension.sdk.test;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.extension.sdk.context.AppBindToolResolver;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:com/kii/extension/sdk/testContext.xml"})
public class TestTemplate {


	@Autowired
	protected AppBindToolResolver resolver;

}
