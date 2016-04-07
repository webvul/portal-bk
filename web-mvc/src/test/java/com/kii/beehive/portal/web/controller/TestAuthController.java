package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.service.PortalSyncUserDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.extension.sdk.entity.KiiUser;

/**
 * the test cases in this class is to test the scenarios of the token stored in auth info cache,
 * the token stored in auth info cache has an expiration
 */
public class TestAuthController extends WebTestTemplate {

	private  static final String AUTH_HEADER = Constants.ACCESS_TOKEN;

    private static final String WEB_CONTEXT_PATH_FOR_TEST = "/beehive-portal/api";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserServiceForTest userServiceForTest;

    @Autowired
    private PortalSyncUserDao beehiveUserDao;

    @Autowired
    private GroupUserRelationDao groupUserRelationDao;

    private String userIDForTest = "userIDForTestAuth";

    private String passwordForTest = DigestUtils.sha1Hex(userIDForTest+"_beehive");

    private String accessToken;

    private String superTokenForTest = BEARER_SUPER_TOKEN;

    private List<Integer> getAllPermissions() throws Exception {
        String result = this.mockMvc.perform(
                get("/permission/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, superTokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response: " + result);

        List<Map<String,Object>> permissionList=mapper.readValue(result, List.class);

        List<Integer> permissionIDList = new ArrayList<>();

        for(Map<String,Object> permission : permissionList) {
            permissionIDList.add((Integer) permission.get("id"));
        }

        return permissionIDList;
    }

    private void setFullPermission(String userID) throws Exception {

        // create user group
        Map<String, Object> request = new HashMap<>();
        request.put("userGroupName", "FullPermissionGroup");

        String ctx= mapper.writeValueAsString(request);

        String result = this.mockMvc.perform(
                post("/usergroup").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, superTokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response: " + result);

        Map<String,Object> map=mapper.readValue(result, Map.class);

        String userGroupID = (String)map.get("userGroupID");

        // add user to user group
        result = this.mockMvc.perform(
                post("/usergroup/" + userGroupID + "/user/" + userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, superTokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response: " + result);

        List<String> userIDList = groupUserRelationDao.findUserIDByUserGroupID(Long.valueOf(userGroupID));
        System.out.println("userIDList: " + userIDList);
        assertTrue(userIDList.contains(userID));

        // get all permissions
        List<Integer> permissionIDList = this.getAllPermissions();

        // set all permissions to user group
        for(Integer permissionID : permissionIDList) {

            result = this.mockMvc.perform(
                    post("/permission/" + permissionID + "/userGroup/" + userGroupID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .header(AUTH_HEADER, superTokenForTest)
            )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            System.out.println("Response: " + result);
        }

    }

    @Before
    public void before() {

        super.before();;

        this.clean();

        this.createUser(userIDForTest, passwordForTest);
        this.createUser(userIDForTest + 1, passwordForTest);

    }

    private void createUser(String userID, String password) {

        KiiUser kiiUser = new KiiUser();
        kiiUser.setLoginName(userID);
        kiiUser.setPassword(password);

        try {
            String kiiUserID = userServiceForTest.createUser(kiiUser);

            BeehiveUser user = new BeehiveUser();
            user.setAliUserID(userID);
            user.setUserName("someUserNameForTest");
            user.setKiiUserID(kiiUserID);

            user.setCompany("someCompanyForTest");
            user.setPhone("somePhoneNumberForTest");
            user.setMail("someMailForTest");

            beehiveUserDao.createUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void clean() {

        this.removeUser(userIDForTest);
        this.removeUser(userIDForTest + 1);

    }

    private void removeUser(String userID) {

        try {
            userServiceForTest.removeUser(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            beehiveUserDao.deleteUser(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String login(String userID, String password) throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userID", userID);
        request.put("password", password);

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/oauth2/login").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        // assert
        String accessToken = (String)map.get("accessToken");
        assertNotNull(map.get("accessToken"));
        assertTrue(accessToken.length() > 0);

        assertEquals(userID, map.get("userID"));
        assertEquals("someUserNameForTest", map.get("userName"));
        assertEquals("somePhoneNumberForTest", map.get("phone"));
        assertEquals("someMailForTest", map.get("mail"));
        assertEquals("someCompanyForTest", map.get("company"));

        return accessToken;
    }

    private void logout(String token) throws Exception {

        this.mockMvc.perform(
                post("/oauth2/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + token)
        )
                .andExpect(status().isOk());

    }

    private void validateTokenAvailable(String token) throws Exception {

        ResultActions resultActions = this.mockMvc.perform(
                post("/oauth2/validatetoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + token)
        )
                .andExpect(status().isOk());

        String result = resultActions.andReturn().getResponse().getContentAsString();

        Map<String, Object> map = (Map<String, Object>)mapper.readValue(result, Map.class);

        String accessToken = (String)map.get("accessToken");
        assertTrue(accessToken.length() > 0);

        String userID = (String)map.get("userID");
        assertTrue(userID.length() > 0);

        List<String> list = (List<String>)map.get("permissions");
        assertTrue(list.size() > 0);

    }

    private void validateTokenUnavailable(String token) throws Exception {

        this.mockMvc.perform(
                post("/oauth2/validatetoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + token)
        )
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testRegister() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userID", userIDForTest);
        request.put("password", "newpassword");

        String ctx= mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/oauth2/register").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk());

    }

    @Test
    public void testRegisterException() throws Exception {

        this.testRegister();

        // registered already
        Map<String, Object> request = new HashMap<>();
        request.put("userID", userIDForTest);
        request.put("password", "newpassword");

        String ctx= mapper.writeValueAsString(request);

        String result = this.mockMvc.perform(
                post("/oauth2/register").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response:" + result);

        // wrong userID
        request = new HashMap<>();
        request.put("userID", "some_non_existing_userID");
        request.put("password", "newpassword");

        ctx= mapper.writeValueAsString(request);

        result = this.mockMvc.perform(
                post("/oauth2/register").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response:" + result);

    }

    @Test
    public void testLogin() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userID", userIDForTest);
        request.put("password", passwordForTest);

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/oauth2/login").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        // assert
        accessToken = (String)map.get("accessToken");
        assertNotNull(map.get("accessToken"));
        assertTrue(accessToken.length() > 0);

        assertEquals(userIDForTest, map.get("userID"));
        assertEquals("someUserNameForTest", map.get("userName"));
        assertEquals("somePhoneNumberForTest", map.get("phone"));
        assertEquals("someMailForTest", map.get("mail"));
        assertEquals("someCompanyForTest", map.get("company"));

    }

    @Test
    public void testLoginException() throws Exception {

        // wrong user id
        Map<String, Object> request = new HashMap<>();
        request.put("userID", "wrongUserID");
        request.put("password", passwordForTest);

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/oauth2/login").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        assertNull(map.get("accessToken"));

        // wrong password
        request = new HashMap<>();
        request.put("userID", userIDForTest);
        request.put("password", "wrongpassword");

        ctx= mapper.writeValueAsString(request);

        result=this.mockMvc.perform(
                post("/oauth2/login").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        map=mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        assertNull(map.get("accessToken"));
    }

    @Test
    public void testLogout() throws Exception {

        this.testLogin();

        this.mockMvc.perform(
                post("/oauth2/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessToken)
        )
                .andExpect(status().isOk());

        // token not valid any more
        Map<String, Object> request = new HashMap<>();
        request.put("oldPassword", passwordForTest);
        request.put("newPassword", passwordForTest + "new");

        String ctx= mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/oauth2/changepassword").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessToken)
        )
                .andExpect(status().isUnauthorized());

    }

    /**
     * need to shorten the token expire time to 15 seconds for below testing
     * below scenario will be tested:
     * 1. user login and gets the token
     * 2. 10 seconds later, user's token is valid, while the other user login and gets token
     * 3. another 10 seconds later, user's token is not valid, while the other user's token is still valid
     *
     * @throws Exception
     */
    @Test
    public void testTokenCache() throws Exception {

        // user login
        this.setFullPermission(userIDForTest);
        String accessTokenOfUser = this.login(userIDForTest, passwordForTest);

        // token is valid
        this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessTokenOfUser)
        )
                .andExpect(status().isOk());

        // sleep 10 seconds
        Thread.sleep(10000);

        // user's token is still valid
        this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessTokenOfUser)
        )
                .andExpect(status().isOk());

        // the other user login
        this.setFullPermission(userIDForTest+1);
        String accessTokenOfTheOtherUser = this.login(userIDForTest+1, passwordForTest);

        // the other user's token is valid
        this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessTokenOfTheOtherUser)
        )
                .andExpect(status().isOk());

        // sleep 10 seconds
        Thread.sleep(10000);

        // user's token is not valid
        this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessTokenOfUser)
        )
                .andExpect(status().isUnauthorized());

        // the other user's token is still valid
        this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessTokenOfTheOtherUser)
        )
                .andExpect(status().isOk());

    }



    @Test
    public void testChangePassword() throws Exception {

        this.testLogin();

        Map<String, Object> request = new HashMap<>();
        request.put("oldPassword", passwordForTest);
        request.put("newPassword", passwordForTest + "new");

        String ctx= mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/oauth2/changepassword").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessToken)
        )
                .andExpect(status().isOk());

        // login with old password
        request = new HashMap<>();
        request.put("userID", userIDForTest);
        request.put("password", passwordForTest);

        ctx= mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/oauth2/login").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest());

        // login with new password
        request = new HashMap<>();
        request.put("userID", userIDForTest);
        request.put("password", passwordForTest + "new");

        ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/oauth2/login").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        assertNotNull(map.get("accessToken"));
    }

    @Test
    public void testChangePasswordException() throws Exception {

        this.testLogin();

        // no newPassword
        Map<String, Object> request = new HashMap<>();
        request.put("oldPassword", passwordForTest);

        String ctx= mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/oauth2/changepassword").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessToken)
        )
                .andExpect(status().isBadRequest());

        // no oldPassword
        request = new HashMap<>();
        request.put("newPassword", passwordForTest+"new");

        ctx= mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/oauth2/changepassword").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + accessToken)
        )
                .andExpect(status().isBadRequest());

        // non existing token
        request = new HashMap<>();
        request.put("oldPassword", passwordForTest);
        request.put("newPassword", passwordForTest + "new");

        ctx = mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/oauth2/changepassword").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + "some_non_existing_token")
        )
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testAuthException() throws Exception{

        /**
         * test user API
         */
        Map<String, Object> request = new HashMap<>();
        request.put("userID", "some_user_id");
        request.put("userName", "张三");
        request.put("company", "IBM");
        request.put("role", "2");
        request.put("phone", "some.phone.number");
        request.put("mail", "some.mail");

        Map<String, Object> custom = new HashMap<>();
        custom.put("age", 40);
        custom.put("division", new String[]{"QA", "Operation"});
        request.put("custom", custom);

        String ctx = mapper.writeValueAsString(request);

        // no accessToken
        this.mockMvc.perform(
                post("/users").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isUnauthorized());

        // wrong accessToken
        this.mockMvc.perform(
                post("/users").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer some_wrong_token")
        )
                .andExpect(status().isUnauthorized());

        /**
         * test user group API
         */
        request = new HashMap<>();
        request.put("userGroupName", "test_usergroupname");
        request.put("description", "some description");

        ctx= mapper.writeValueAsString(request);

        // no accessToken
        this.mockMvc.perform(
                post("/usergroup/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isUnauthorized());

        // wrong accessToken
        this.mockMvc.perform(
                post("/usergroup/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer some_wrong_token")
        )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();


        /**
         * test thing API
         */
        request = new HashMap<>();
        request.put("vendorThingID", "somevendorhingid");
        request.put("kiiAppID", "someappid");
        request.put("type", "some type");
        request.put("location", "some location");

        ctx= mapper.writeValueAsString(request);

        // no access token
        this.mockMvc.perform(
                post("/things").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isUnauthorized());

        // wrong access token
        this.mockMvc.perform(
                post("/things").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer some_wrong_token")
        )
                .andExpect(status().isUnauthorized());

        /**
         * test tag API
         */
        request = new HashMap<>();
        request.put("displayName", "some_display_name");
        request.put("description", "some description");

        ctx= mapper.writeValueAsString(request);

        // no access token
        this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isUnauthorized());

        // wrong access token
        this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer some_wrong_token")
        )
                .andExpect(status().isUnauthorized());

        /**
         * test auth API
         */
        request = new HashMap<>();
        request.put("oldPassword", passwordForTest);
        request.put("newPassword", passwordForTest + "new");

        mapper.writeValueAsString(request);

        // no token
        this.mockMvc.perform(
                post("/oauth2/changepassword").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isUnauthorized());

        // wrong token
        this.mockMvc.perform(
                post("/oauth2/changepassword").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer some_wrong_token")
        )
                .andExpect(status().isUnauthorized());

    }



}

