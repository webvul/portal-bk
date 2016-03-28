package com.kii.beehive.portal.web.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TestDeviceSupplierDao extends WebTestTemplate {

    private  static final String AUTH_HEADER = Constants.ACCESS_TOKEN;

    private String superTokenForTest = BEARER_SUPER_TOKEN;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testGetAllDeviceSuppliers() throws Exception {

        String result=this.mockMvc.perform(
                get("/devicesuppliers/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, superTokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String,Object>> list=mapper.readValue(result, List.class);

        System.out.println("Response: " + list);

        // assume 3 device suppliers in internal dev env
        assertTrue(list.size() == 3);

        // assert http return
        for (Map<String, Object> map : list) {
            assertTrue(!Strings.isBlank("name"));
            assertTrue(!Strings.isBlank("relationAppName"));
            assertTrue(!Strings.isBlank("relationAppID"));
        }

    }


}

