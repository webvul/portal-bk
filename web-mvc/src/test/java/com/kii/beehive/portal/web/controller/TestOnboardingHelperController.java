package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.WebTestTemplate;


public class TestOnboardingHelperController extends WebTestTemplate {

    @Autowired
    private GlobalThingSpringDao thingDao;

    @Autowired
    private ObjectMapper mapper;

    private String vendorThingIDForTest;

    private Long globalThingIDForTest;

    private final static String KII_APP_ID = "0af7a7e7";


	@Before
    public void before(){
        super.before();

        after();

        vendorThingIDForTest = "vendor_id_for_test";
        globalThingIDForTest = 100l;

    }

    @After
    public void after() {

        GlobalThingInfo thingInfo = thingDao.getThingByVendorThingID(vendorThingIDForTest);
        if(thingInfo != null) {
            try {
                thingDao.deleteByID(thingInfo.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        try {
            thingDao.deleteByID(globalThingIDForTest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetOnboardingInfo() throws Exception {

        GlobalThingInfo thingInfo = new GlobalThingInfo();
        thingInfo.setVendorThingID(vendorThingIDForTest);
        thingInfo.setKiiAppID(KII_APP_ID);
        thingDao.saveOrUpdate(thingInfo);

        String result=this.mockMvc.perform(
                get("/onboardinghelper/" + vendorThingIDForTest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", BEARER_SUPER_TOKEN)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        System.out.println(map);

        // assert http return
        assertEquals(KII_APP_ID, map.get("kiiAppID"));
        assertEquals("f973edcaaec9aeac36dd01ebe1c3bc49", map.get("kiiAppKey"));
        assertEquals("https://api-development-beehivecn3.internal.kii.com", map.get("kiiSiteUrl"));
        assertNotNull(map.get("ownerID"));
        assertNotNull(map.get("ownerToken"));

    }

    @Test
    public void testGetOnboardingInfoException() throws Exception {

        String result=this.mockMvc.perform(
                get("/onboardinghelper/" + "some_non_existing_vendorThingID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", BEARER_SUPER_TOKEN)
        )
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();


    }

	@Test
	public  void testAddApp() throws Exception {

		int status=this.mockMvc.perform(
				post("/appRegist/" + "ec08d20c")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header("Authorization","Bearer super_token")

		).andReturn().getResponse().getStatus();

		assertEquals(200,status);

		System.in.read();

	}

	@Test
	public  void testAppInit() throws Exception {


		int status=this.mockMvc.perform(
				post("/appinit")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header("Authorization","Bearer super_token")
						.content("{}")

		).andReturn().getResponse().getStatus();

		assertEquals(200,status);

		System.in.read();

	}

}
