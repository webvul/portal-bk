package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.web.config.WebMvcConfig;
import com.kii.beehive.portal.web.constant.Constants;

import test.mock.context.TestContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes= {WebMvcConfig.class,TestContext.class})
public class LocControllerTest  {
	
	protected String BEARER_SUPER_TOKEN = "Bearer " + Constants.SUPER_TOKEN;
	
	protected static final String AUTH_HEADER = Constants.ACCESS_TOKEN;
	
	@Autowired
	protected WebApplicationContext wac;
	
	@Autowired
	private ObjectMapper mapper;
	
	protected MockMvc mockMvc;
	
	@BeforeClass
	public static void setSystemProps() {
		System.setProperty("spring.profile","local");
	}
	
	@Before
	public void before(){
		
		
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(new MockMvcConfigurer(){
			
			@Override
			public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
				
			}
			
			@Override
			public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {
				
				return null;
			}
		}).defaultRequest(MockMvcRequestBuilders.get("/foo")
				.header(AUTH_HEADER, BEARER_SUPER_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8"))
				.build();
	}
	
	
	@Test
	public void testGetLocTree() throws Exception {
		
		Map<String,Object> map=new HashMap<>();
		/*
		{"password":"123456",
 "userName":"test1114" }
		 */
		map.put("password","123456");
		map.put("userName","test1114");
		
		
//		String result=this.mockMvc.perform(
//				MockMvcRequestBuilders.post("/oauth2/login").content(mapper.writeValueAsBytes(map))
//		).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
//
//		Map<String,Object>  resultMap=mapper.readValue(result,Map.class);
		
		
		long start=System.currentTimeMillis();
		
		for(int i=0;i<10;i++) {
			this.mockMvc.perform( MockMvcRequestBuilders.get("/locationTags/fullTree")
//					.header(AUTH_HEADER,"Bearer "+resultMap.get("accessToken"))
					.header(AUTH_HEADER,BEARER_SUPER_TOKEN)
			)
					.andExpect(status().isOk()).andReturn();
			
			long end=System.currentTimeMillis();
			
			System.out.println("inteval:"+(end-start));
			assertTrue(end-start<3*1000);
			
			start=end;
		}
	}
	
	
	@Test
	public void testPermissTree() throws Exception {
		this.mockMvc.perform(
				MockMvcRequestBuilders.post("/oauth2/validateLoginAccessToken")
		).andExpect(status().isOk()).andReturn();
	}
	
	
	
	
}
