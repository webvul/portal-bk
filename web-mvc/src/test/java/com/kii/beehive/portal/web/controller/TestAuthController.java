package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.extension.sdk.entity.KiiUser;

/**
 * Created by USER on 12/1/15.
 */
public class TestAuthController extends WebTestTemplate {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserServiceForTest userServiceForTest;

    @Autowired
    private BeehiveUserDao beehiveUserDao;

    private String userIDForTest = "userIDForTestAuth";

    private String passwordForTest = DigestUtils.sha1Hex(userIDForTest+"_beehive");

    private String accessToken;

    @Before
    public void before() {

        super.before();;

        this.clean();

        KiiUser kiiUser = new KiiUser();
        kiiUser.setLoginName(userIDForTest);
        kiiUser.setPassword(passwordForTest);

        try {
            String kiiUserID = userServiceForTest.createUser(kiiUser);

            BeehiveUser user = new BeehiveUser();
            user.setAliUserID(userIDForTest);
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

        try {
            userServiceForTest.removeUser(userIDForTest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            beehiveUserDao.deleteUser(userIDForTest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * must run in new thread
     * @throws Exception
     */
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

    /**
     * must run in new thread
     * @throws Exception
     */
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

    /**
     * must run in new thread
     * @throws Exception
     */
    @Test
    public void testLogout() throws Exception {

        this.testLogin();

        this.mockMvc.perform(
                post("/oauth2/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("accessToken", "Bearer " + accessToken)
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
                        .header("accessToken", "Bearer " + accessToken)
        )
                .andExpect(status().isUnauthorized());

    }

    /**
     * need to shorten the "TOKEN_VALID_TIME_IN_MILLISECOND" to 15 seconds for below testing
     * @throws Exception
     */
    @Test
    public void testTokenCache() throws Exception {

        this.testLogin();

        // token is valid
        this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, "Bearer " + accessToken)
        )
                .andExpect(status().isOk());

        // sleep 5 seconds
        Thread.sleep(5000);

        // token is still valid
        this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, "Bearer " + accessToken)
        )
                .andExpect(status().isOk());

        // sleep 15 seconds
        Thread.sleep(15000);

        // token is not valid
        this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, "Bearer " + accessToken)
        )
                .andExpect(status().isUnauthorized());


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
                        .header("accessToken", "Bearer " + accessToken)
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

    /**
     * must run in new thread
     * @throws Exception
     */
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
                        .header("accessToken", "Bearer " + accessToken)
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
                        .header("accessToken", "Bearer " + accessToken)
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
                        .header("accessToken", "Bearer " + "some_non_existing_token")
        )
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testConstantsException() throws Exception{

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
                post("/users/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", "Bearer d31032a0-8ebf-11e5-9560-00163e02138f")
        )
                .andExpect(status().isUnauthorized());

        // wrong accessToken
        this.mockMvc.perform(
                post("/users/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", "Bearer d31032a0-8ebf-11e5-9560-00163e02138f")
                        .header(Constants.ACCESS_TOKEN, "Bearer some_wrong_token")
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
                        .header(Constants.ACCESS_TOKEN, "Bearer some_wrong_token")
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
                        .header(Constants.ACCESS_TOKEN, "Bearer some_wrong_token")
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
                        .header(Constants.ACCESS_TOKEN, "Bearer some_wrong_token")
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
                        .header(Constants.ACCESS_TOKEN, "Bearer some_wrong_token")
        )
                .andExpect(status().isUnauthorized());

    }



}

