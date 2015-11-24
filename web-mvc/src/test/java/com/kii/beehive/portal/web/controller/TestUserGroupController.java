package com.kii.beehive.portal.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserGroupDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.WebTestTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.*;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUserGroupController extends WebTestTemplate {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BeehiveUserDao userDao;

    @Autowired
    private BeehiveUserGroupDao userGroupDao;

    private List<String> userIDListForTest = new ArrayList<>();

    private String userGroupID;

    @Before
    public void before() {

        userIDListForTest.add("test.userid.1");
        userIDListForTest.add("test.userid.2");
        userIDListForTest.add("test.userid.3");

        for(String s : userIDListForTest) {
            BeehiveUser user = new BeehiveUser();
            user.setAliUserID(s);
            user.setUserName("username.for." + s);

            userDao.createUser(user);
        }

    }

    @After
    public void after() {

        for(String s : userIDListForTest) {
            userDao.deleteUser(s);
        }

        userGroupDao.deleteUserGroup(userGroupID);

    }

    @Test
    public void testCreateUserGroup() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", "test.usergroupname");
        request.put("description", "some description");

        // set users
        List<String> userIDList = new ArrayList<>();
        userIDList.addAll(userIDListForTest);

        request.put("users", userIDList);

        // set custom
        Map<String, Object> custom = new HashMap<>();
        custom.put("birthday", "20001230");
        custom.put("gender", "male");
        request.put("custom", custom);

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/usergroup/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        userGroupID = (String)map.get("userGroupID");
        assertNotNull(userGroupID);

        // assert DB
        BeehiveUserGroup userGroup = userGroupDao.getUserGroupByID(userGroupID);
        assertEquals(userGroupID, userGroup.getUserGroupID());
        assertEquals("test.usergroupname", userGroup.getUserGroupName());
        assertEquals("some description", userGroup.getDescription());

        assertEquals(3, userGroup.getUsers().size());
        assertTrue(userGroup.getUsers().containsAll(userIDList));

        assertEquals(2, userGroup.getCustomFields().size());
        assertEquals("20001230", userGroup.getCustomFields().get("birthday"));
        assertEquals("male", userGroup.getCustomFields().get("gender"));

    }

    @Test
    public void testUpdateUserGroup() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", "test.usergroupname.new");
        request.put("description", "some description.new");

        // set users
        List<String> userIDList = new ArrayList<>();
        userIDList.addAll(userIDListForTest);
        userIDList.remove(0);

        request.put("users", userIDList);

        // set custom
        Map<String, Object> custom = new HashMap<>();
        custom.put("birthday", 123.45);
        custom.put("nationality", "new field during update");
        request.put("custom", custom);

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                patch("/usergroup/" + userGroupID).content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        String expectedUserGroupID = userGroupID;
        userGroupID = (String)map.get("userGroupID");
        assertEquals(expectedUserGroupID, userGroupID);

        // assert DB
        BeehiveUserGroup userGroup = userGroupDao.getUserGroupByID(userGroupID);
        assertEquals(userGroupID, userGroup.getUserGroupID());
        assertEquals("test.usergroupname.new", userGroup.getUserGroupName());
        assertEquals("some description.new", userGroup.getDescription());

        assertEquals(2, userGroup.getUsers().size());
        assertTrue(userGroup.getUsers().containsAll(userIDList));

        assertEquals(3, userGroup.getCustomFields().size());
        assertEquals(123.45, userGroup.getCustomFields().get("birthday"));
        assertEquals("male", userGroup.getCustomFields().get("gender"));
        assertEquals("new field during update", userGroup.getCustomFields().get("nationality"));

    }

    @Test
    public void testQueryUserGroup() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupID", userGroupID);
        request.put("userGroupName", "test.usergroupname.new");
        request.put("includeUserData", "1");

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                patch("/usergroup/simplequery" + userGroupID).content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        String expectedUserGroupID = userGroupID;
        userGroupID = (String)map.get("userGroupID");
        assertEquals(expectedUserGroupID, userGroupID);

        assertEquals("test.usergroupname.new", map.get("userGroupName"));
        assertEquals("some description.new", map.get("description"));

        assertEquals(2, ((List)map.get("users")).size());
        for(Object s : ((List)map.get("users"))) {
            Map<String, Object> user = (Map)s;
            String id = (String)user.get("aliUserID");
            String name = (String)user.get("userName");

            assertTrue(userIDListForTest.contains(id));
            assertEquals("username.for." + id, name);
        }

        assertEquals(3, ((Map)map.get("custom")).size());
        assertEquals(123.45, ((Map)map.get("custom")).get("birthday"));
        assertEquals("male", ((Map)map.get("custom")).get("gender"));
        assertEquals("new field during update", ((Map)map.get("custom")).get("nationality"));

    }

    @Test
    public void testDeleteUserGroup() throws Exception {

        String result=this.mockMvc.perform(
                delete("/usergroup/" + userGroupID)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        assertEquals(userGroupID, map.get("userGroupID"));

        BeehiveUserGroup userGroup = userGroupDao.getUserGroupByID(userGroupID);
        assertNull(userGroup);

    }

}
