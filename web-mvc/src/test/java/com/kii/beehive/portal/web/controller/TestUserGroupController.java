package com.kii.beehive.portal.web.controller;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserGroupDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.beehive.portal.web.WebTestTemplate;

public class TestUserGroupController extends WebTestTemplate {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BeehiveUserDao userDao;

    @Autowired
    private BeehiveUserGroupDao userGroupDao;
    
    private List<String> userIDListForTest = new ArrayList<>();

    private String userGroupID;

    private List<String> userGroupNameListForTest = new ArrayList<>();

    @Before
    public void before() {
    	super.before();

        userGroupNameListForTest.add("test_usergroupname");
        userGroupNameListForTest.add("test_usergroupname_new");
        userGroupNameListForTest.add("test_usergroupname_withoutuser");

        System.out.println("before to delete user group");
        clear();
        System.out.println("after to delete user group");

        userIDListForTest.add("test_userid_1");
        userIDListForTest.add("test_userid_2");
        userIDListForTest.add("test_userid_3");

        for(String s : userIDListForTest) {
            BeehiveUser user = new BeehiveUser();
            user.setAliUserID(s);
            user.setUserName("username_for_" + s);

            userDao.createUser(user);
        }

    }

    @After
    public void clear() {

        for(String s : userIDListForTest) {
            try {
                userDao.deleteUser(s);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(String userGroupName : userGroupNameListForTest) {
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("userGroupName", userGroupName);
                userGroupID = userGroupDao.getUserGroupsBySimpleQuery(param).get(0).getUserGroupID();
                userGroupDao.deleteUserGroup(userGroupID);
                System.out.println("success to delete user group:" + userGroupID);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCreateUserGroup() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", "test_usergroupname");
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
        assertEquals("test_usergroupname", userGroup.getUserGroupName());
        assertEquals("some description", userGroup.getDescription());

        assertEquals(3, userGroup.getUsers().size());
        assertTrue(userGroup.getUsers().containsAll(userIDList));

        assertEquals(2, userGroup.getCustom().size());
        assertEquals("20001230", userGroup.getCustom().get("birthday"));
        assertEquals("male", userGroup.getCustom().get("gender"));

    }

    @Test
    public void testUpdateUserGroup() throws Exception {

        // create user group info
        testCreateUserGroup();

        // update user group info
        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", "test_usergroupname_new");
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
        assertEquals("test_usergroupname_new", userGroup.getUserGroupName());
        assertEquals("some description.new", userGroup.getDescription());

        assertEquals(2, userGroup.getUsers().size());
        assertTrue(userGroup.getUsers().containsAll(userIDList));

        assertEquals(3, userGroup.getCustom().size());
        assertEquals(123.45, userGroup.getCustom().get("birthday"));
        assertEquals("male", userGroup.getCustom().get("gender"));
        assertEquals("new field during update", userGroup.getCustom().get("nationality"));

    }

    @Test
    public void testQueryUserGroup() throws Exception {

        // create user group info
        testUpdateUserGroup();

        // test query

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupID", userGroupID);
        request.put("userGroupName", "test_usergroupname_new");
        request.put("includeUserData", "1");

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/usergroup/simplequery").content(ctx)
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

        assertEquals("test_usergroupname_new", map.get("userGroupName"));
        assertEquals("some description.new", map.get("description"));

        assertEquals(2, ((List)map.get("users")).size());
        for(Object s : ((List)map.get("users"))) {
            System.out.println("user: " + s);
            Map<String, Object> user = (Map)s;
            String id = (String)user.get("userID");
            String name = (String)user.get("userName");

            assertTrue(userIDListForTest.contains(id));
            assertEquals("username_for_" + id, name);
        }

    }

    @Test
    public void testQueryUserGroupNoResult() throws Exception {

        // create user group info
        testUpdateUserGroup();

        // test query

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupID", "nonExistingUserGroupID");
        request.put("includeUserData", "1");

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/usergroup/simplequery").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(result);
        // assert http reture
        assertTrue(result.length() == 0);

    }

    @Test
    public void testQueryUserGroupNoUser() throws Exception {

        String userGroupName = "test_usergroupname_withoutuser";

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", userGroupName);
        request.put("description", "some description");

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/usergroup/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http return
        userGroupID = (String)map.get("userGroupID");
        assertNotNull((String)map.get("userGroupID"));

        // test query

        // includeUserData == 1
        request = new HashMap<>();
        request.put("userGroupName", userGroupName);
        request.put("includeUserData", "1");

        ctx= mapper.writeValueAsString(request);

        result=this.mockMvc.perform(
                post("/usergroup/simplequery").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        map=mapper.readValue(result, Map.class);

        // assert http return
        String expectedUserGroupID = userGroupID;
        userGroupID = (String)map.get("userGroupID");
        assertEquals(expectedUserGroupID, userGroupID);

        assertEquals(userGroupName, map.get("userGroupName"));

        assertTrue(((List)map.get("users")).size() == 0);

        // includeUserData == 0
        request = new HashMap<>();
        request.put("userGroupName", userGroupName);
        request.put("includeUserData", "0");

        ctx= mapper.writeValueAsString(request);

        result=this.mockMvc.perform(
                post("/usergroup/simplequery").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        map=mapper.readValue(result, Map.class);

        // assert http return
        expectedUserGroupID = userGroupID;
        userGroupID = (String)map.get("userGroupID");
        assertEquals(expectedUserGroupID, userGroupID);

        assertEquals(userGroupName, map.get("userGroupName"));

        assertTrue(((List)map.get("users")).size() == 0);

    }


    @Test
    public void testQueryUserGroupAll() throws Exception {

        // test query

        Map<String, Object> request = new HashMap<>();

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/usergroup/simplequery").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String,Object>> map=mapper.readValue(result, List.class);

        // assert http reture
        assertTrue(map.size() > 0);
        for(Map<String, Object> group : map) {
            System.out.println("user group: " + group);
        }

    }

    @Test
    public void testDeleteUserGroup() throws Exception {

        // create user group info
        testCreateUserGroup();

        String result=this.mockMvc.perform(
                delete("/usergroup/" + userGroupID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        BeehiveUserGroup userGroup = userGroupDao.getUserGroupByID(userGroupID);
        assertNull(userGroup);

    }

}
