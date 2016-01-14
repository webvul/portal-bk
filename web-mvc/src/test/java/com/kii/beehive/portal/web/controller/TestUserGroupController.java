package com.kii.beehive.portal.web.controller;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;

public class TestUserGroupController extends WebTestTemplate {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BeehiveUserDao userDao;

    @Autowired
    private GroupUserRelationDao relationDao;
    
    private List<String> userIDListForTest = new ArrayList<>();

    private String userGroupID;

    private List<String> userGroupNameListForTest = new ArrayList<>();

    private String tokenForTest = "Bearer " + Constants.SUPER_TOKEN;

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
                System.out.println("success to delete user group:" + userGroupID);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String toString(List<String> list) {

        StringBuffer buffer = new StringBuffer();
        for(String str : list) {
            buffer.append(",");
            buffer.append(str);
        }
        buffer.deleteCharAt(0);

        return buffer.toString();
    }

    @Test
    public void testAddUsersToUserGroup() throws Exception {
        this.testCreateUserGroup();

        // add users to user group
        String userIDs = this.toString(userIDListForTest);

        String result=this.mockMvc.perform(
                post("/usergroup/" + userGroupID + "/users/" + userIDs)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response: " + result);

        // assert
        List<String> userIDList = relationDao.findUserIDByUserGroupID(Long.valueOf(userGroupID));
        System.out.println("userIDList: " + userIDList);

        assertTrue(userIDList.containsAll(userIDListForTest));

    }

    @Test
    public void testRemoveUsersFromUserGroup() throws Exception {
        // create user group
        this.testAddUsersToUserGroup();

        // remove users from user group
        String userIDLeft = userIDListForTest.remove(0);
        String userIDs = this.toString(userIDListForTest);

        String result=this.mockMvc.perform(
                delete("/usergroup/" + userGroupID + "/users/" + userIDs)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response: " + result);

        // assert
        List<String> userIDList = relationDao.findUserIDByUserGroupID(Long.valueOf(userGroupID));
        System.out.println("userIDList: " + userIDList);

        assertTrue(userIDList.contains(userIDLeft));

        for(String userID : userIDListForTest) {
            assertTrue(!userIDList.contains(userID));
        }

    }

    @Test
    public void testCreateUserGroup() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", "test_usergroupname");
        request.put("description", "some description");

//        // set users
//        List<String> userIDList = new ArrayList<>();
//        userIDList.addAll(userIDListForTest);
//
//        request.put("users", userIDList);
//
//        // set custom
//        Map<String, Object> custom = new HashMap<>();
//        custom.put("birthday", "20001230");
//        custom.put("gender", "male");
//        request.put("custom", custom);

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/usergroup/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http return
        userGroupID = (String)map.get("userGroupID");
        assertNotNull(userGroupID);


//        // assert user in DB
//        List<BeehiveUser> beehiveUserList = userDao.getUserByIDs(userIDListForTest);
//        for(BeehiveUser beehiveUser : beehiveUserList) {
//            assertTrue(beehiveUser.getGroups().contains(userGroupID));
//        }

    }

    @Test
    public void testCreateUserGroupException() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", "test_usergroupname");
        request.put("description", "some description");

        // set users
        List<String> userIDList = new ArrayList<>();
        userIDList.addAll(userIDListForTest);
        userIDList.add("non_existing_userid1");
        userIDList.add("non_existing_userid2");

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
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response: " + result);
        assertTrue(result.indexOf("non_existing_userid1") > -1);
        assertTrue(result.indexOf("non_existing_userid2") > -1);

    }

    @Test
    public void testUpdateUserGroup() throws Exception {

        // create user group info
        testCreateUserGroup();

        // update user group info
        Map<String, Object> request = new HashMap<>();
        request.put("userGroupID", userGroupID);
        request.put("userGroupName", "test_usergroupname_new");
        request.put("description", "some description.new");

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/usergroup/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        String expectedUserGroupID = userGroupID;
        userGroupID = (String)map.get("userGroupID");
        assertEquals(expectedUserGroupID, userGroupID);

    }

    @Test
    public void testUpdateUserGroupException() throws Exception {

        // create user group info
        testCreateUserGroup();

        // update user group info
        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", "test_usergroupname_new");
        request.put("description", "some description.new");

        // set users
        List<String> userIDList = new ArrayList<>();
        userIDList.add("non_existing_userid1");
        userIDList.add("non_existing_userid2");
        userIDList.addAll(userIDListForTest);
        userIDList.remove(userIDList.size() - 1);

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
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response: " + result);
        assertTrue(result.indexOf("non_existing_userid1") > -1);
        assertTrue(result.indexOf("non_existing_userid2") > -1);

    }

    @Test
    public void testQueryUserGroup() throws Exception {

        // create user group info
        testUpdateUserGroup();

        // query
        String result=this.mockMvc.perform(
                get("/usergroup/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String,Object>> list = mapper.readValue(result, List.class);

        System.out.println("Response: " + list);

        // assert
        assertTrue(list.size() > 0);

        Map<String,Object> map = list.get(0);

        // assert http return
        String expectedUserGroupID = userGroupID;
        userGroupID = (String)map.get("userGroupID");
        assertEquals(expectedUserGroupID, userGroupID);

        assertEquals("test_usergroupname_new", map.get("userGroupName"));
        assertEquals("some description.new", map.get("description"));

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
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
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
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
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
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
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
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
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

        // create user group info
        testUpdateUserGroup();

        // query
        String result=this.mockMvc.perform(
                get("/usergroup/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String,Object>> list = mapper.readValue(result, List.class);

        System.out.println("Response: " + list);

        // assert
        assertTrue(list.size() > 0);

        Map<String,Object> map = list.get(0);

        // assert http return
        String expectedUserGroupID = userGroupID;
        userGroupID = (String)map.get("userGroupID");
        assertEquals(expectedUserGroupID, userGroupID);

        assertEquals("test_usergroupname_new", map.get("userGroupName"));
        assertEquals("some description.new", map.get("description"));

    }

    @Test
    public void testDeleteUserGroup() throws Exception {

        // create user group info
        testCreateUserGroup();

        String result=this.mockMvc.perform(
                delete("/usergroup/" + userGroupID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        // assert user in DB
        List<BeehiveUser> beehiveUserList = userDao.getUserByIDs(userIDListForTest);
        for(BeehiveUser beehiveUser : beehiveUserList) {
            assertTrue(!beehiveUser.getGroups().contains(userGroupID));
        }
    }

}
