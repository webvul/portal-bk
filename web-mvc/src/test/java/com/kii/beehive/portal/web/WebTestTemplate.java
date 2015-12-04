package com.kii.beehive.portal.web;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/portalWebContext.xml")
public class WebTestTemplate {

	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;
	
	@BeforeClass
	public static void setSystemProps() {
		System.setProperty("spring.profile","test");
	}

	@Before
	public void before(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void emptyTestInWebTestTemplate() {
		// this method is used to void "java.lang.Exception: No runnable methods" in this class
	}


}
