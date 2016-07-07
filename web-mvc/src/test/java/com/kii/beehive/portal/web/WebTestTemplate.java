package com.kii.beehive.portal.web;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.kii.beehive.portal.web.config.WebMvcConfig;
import com.kii.beehive.portal.web.constant.Constants;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes= WebMvcConfig.class)
@Transactional
@Rollback
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

	protected String BEARER_SUPER_TOKEN = "Bearer " + Constants.SUPER_TOKEN;

	protected String BEARER_DEVICE_SUPPLIER_ID = "Bearer d31032a0-8ebf-11e5-9560-00163e02138f";

	protected static final String WEB_CONTEXT_PATH_FOR_TEST = "/beehive-portal/api";

	/**
	 * this method is added to resolve the junit problem that in mockMvc HttpServletRequest.getRequestURI() doesn't include the context path
	 *
	 * @param uri
	 * @return
	 */
	protected MockHttpServletRequestBuilder post(String uri) {
		return MockMvcRequestBuilders.post(WEB_CONTEXT_PATH_FOR_TEST + uri).contextPath(WEB_CONTEXT_PATH_FOR_TEST);
	}

	/**
	 * this method is added to resolve the junit problem that in mockMvc HttpServletRequest.getRequestURI() doesn't include the context path
	 *
	 * @param uri
	 * @return
	 */
	protected MockHttpServletRequestBuilder get(String uri) {
		return MockMvcRequestBuilders.get(WEB_CONTEXT_PATH_FOR_TEST + uri).contextPath(WEB_CONTEXT_PATH_FOR_TEST);
	}

	/**
	 * this method is added to resolve the junit problem that in mockMvc HttpServletRequest.getRequestURI() doesn't include the context path
	 *
	 * @param uri
	 * @return
	 */
	protected MockHttpServletRequestBuilder patch(String uri) {
		return MockMvcRequestBuilders.patch(WEB_CONTEXT_PATH_FOR_TEST + uri).contextPath(WEB_CONTEXT_PATH_FOR_TEST);
	}

	/**
	 * this method is added to resolve the junit problem that in mockMvc HttpServletRequest.getRequestURI() doesn't include the context path
	 *
	 * @param uri
	 * @return
	 */
	protected MockHttpServletRequestBuilder put(String uri) {
		return MockMvcRequestBuilders.put(WEB_CONTEXT_PATH_FOR_TEST + uri).contextPath(WEB_CONTEXT_PATH_FOR_TEST);
	}

	/**
	 * this method is added to resolve the junit problem that in mockMvc HttpServletRequest.getRequestURI() doesn't include the context path
	 *
	 * @param uri
	 * @return
	 */
	protected MockHttpServletRequestBuilder delete(String uri) {
		return MockMvcRequestBuilders.delete(WEB_CONTEXT_PATH_FOR_TEST + uri).contextPath(WEB_CONTEXT_PATH_FOR_TEST);
	}


}
