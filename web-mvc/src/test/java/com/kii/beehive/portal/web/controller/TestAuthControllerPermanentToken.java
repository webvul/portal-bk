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
import com.kii.beehive.portal.store.entity.PortalSyncUser;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.extension.sdk.entity.KiiUser;

/**
 * the test cases in this class is to test the scenarios of the token stored in auth info cache,
 * the token stored in auth info cache has an expiration
 */
public class TestAuthControllerPermanentToken extends WebTestTemplate {

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

//        List<String> userIDList = groupUserRelationDao.findUserIDByUserGroupID(Long.valueOf(userGroupID));
//        System.out.println("userIDList: " + userIDList);
//        assertTrue(userIDList.contains(userID));

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

			PortalSyncUser user = new PortalSyncUser();
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

    private String login(String userID, String password, boolean permanentToken) throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("userID", userID);
        request.put("password", password);
        request.put("permanentToken", permanentToken);

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

    /**
     * need to shorten the token expire time to 15 seconds for below testing
     * 1. login and get permanent token
     * 2. after 20 seconds, token is still available
     * 3. after logout, token is not available at once
     *
     * @throws Exception
     */
    @Test
    public void testLoginPermanentTokenAndLogout() throws Exception {

        // set full permission
        this.setFullPermission(userIDForTest);

        // 1. login and get permanent token
        String token = this.login(userIDForTest, passwordForTest, true);

        this.validateTokenAvailable(token);

        // 2. after 20 seconds, token is still available
        Thread.sleep(20000);

        this.validateTokenAvailable(token);

        // 3. after logout, token is not available at once
        this.logout(token);

        this.validateTokenUnavailable(token);

    }

    /**
     * this test case is to test no conflict between normal token and permanent token on the same user
     * need to shorten the token expire time to 15 seconds for below testing
     *
     * 1. login user and get permanent token "tokenA"
     * 2. login user and get permanent token "tokenB"
     * 3. login user and get normal token "tokenC"
     * 4. after 20 seconds, "tokenC" is not available, while "tokenA" and "tokenB" are still available
     * 5. after logout "tokenA", "tokenA" is not available, while "tokenB" is still available
     *
     * @throws Exception
     */
    @Test
    public void testLoginPermanentTokenAndLogoutForSingleUserScenario() throws Exception {

        // set full permission
        this.setFullPermission(userIDForTest);

        // 1. login user and get permanent token "tokenA"
        String tokenA = this.login(userIDForTest, passwordForTest, true);

        this.validateTokenAvailable(tokenA);

        // 2. login user and get permanent token "tokenB"
        String tokenB = this.login(userIDForTest, passwordForTest, true);

        this.validateTokenAvailable(tokenB);

        // 3. login user and get normal token "tokenC"
        String tokenC = this.login(userIDForTest, passwordForTest, false);

        this.validateTokenAvailable(tokenC);

        // 4. after 20 seconds, "tokenC" is not available, while "tokenA" and "tokenB" are still available
        Thread.sleep(20000);

        this.validateTokenAvailable(tokenA);
        this.validateTokenAvailable(tokenB);
        this.validateTokenUnavailable(tokenC);


        // 5. after logout "tokenA", "tokenA" is not available, while "tokenB" is still available
        this.logout(tokenA);

        this.validateTokenUnavailable(tokenA);
        this.validateTokenAvailable(tokenB);
        this.validateTokenUnavailable(tokenC);

    }

    /**
     * this test case is to test no conflict between normal token and permanent token on the same user
     * need to shorten the token expire time to 15 seconds for below testing
     *
     * 1. login user and get normal token "tokenA"
     * 2. login user and get permanent token "tokenB"
     * 3. login user and get permanent token "tokenC"
     * 4. after logout "tokenB", "tokenB" is not available, while "tokenA" and "tokenC" are still available
     * 5. after 20 seconds, "tokenA" is not available, while "tokenC" is still available
     *
     * @throws Exception
     */
    @Test
    public void testLoginPermanentTokenAndLogoutForSingleUserScenario1() throws Exception {

        // set full permission
        this.setFullPermission(userIDForTest);

        // 1. login user and get normal token "tokenA"
        String tokenA = this.login(userIDForTest, passwordForTest, false);

        this.validateTokenAvailable(tokenA);

        // 2. login user and get permanent token "tokenB"
        String tokenB = this.login(userIDForTest, passwordForTest, true);

        this.validateTokenAvailable(tokenB);

        // 3. login user and get permanent token "tokenC"
        String tokenC = this.login(userIDForTest, passwordForTest, true);

        this.validateTokenAvailable(tokenC);

        // 4. after logout "tokenB", "tokenB" is not available, while "tokenA" and "tokenC" are still available
        this.logout(tokenB);

        this.validateTokenAvailable(tokenA);
        this.validateTokenUnavailable(tokenB);
        this.validateTokenAvailable(tokenC);


        // 5. after 20 seconds, "tokenA" is not available, while "tokenC" is still available
        Thread.sleep(20000);

        this.validateTokenUnavailable(tokenA);
        this.validateTokenUnavailable(tokenB);
        this.validateTokenAvailable(tokenC);

    }

    /**
     * this test case is to test no conflict between the permanent tokens from different users
     * need to shorten the token expire time to 15 seconds for below testing
     *
     * 1. login user "user1" and get permanent token "tokenA"
     * 2. login user "user2" and get permanent token "tokenB"
     * 3. after "user1" logout, "tokenA" is not available, while "tokenB" is still available
     *
     * @throws Exception
     */
    @Test
    public void testLoginPermanentTokenAndLogoutForMultipleUsersScenario() throws Exception {

        // set full permission
        this.setFullPermission(userIDForTest);
        this.setFullPermission(userIDForTest+1);

        // 1. login user "user1" and get permanent token "tokenA"
        String tokenA = this.login(userIDForTest, passwordForTest, true);

        this.validateTokenAvailable(tokenA);

        // 2. login user "user2" and get permanent token "tokenB"
        String tokenB = this.login(userIDForTest+1, passwordForTest, true);

        this.validateTokenAvailable(tokenB);

        // 3. after "user1" logout, "tokenA" is not available, while "tokenB" is still available
        this.logout(tokenA);

        this.validateTokenUnavailable(tokenA);
        this.validateTokenAvailable(tokenB);

    }

    /**
     * this test case is to test no conflict between the normal/permanent tokens from different users
     * need to shorten the token expire time to 15 seconds for below testing
     *
     * 1. login user "user1" and get normal token "tokenA"
     * 2. login user "user2" and get permanent token "tokenB"
     * 3. after "user2" logout, "tokenB" is not available, while "tokenA" is still available
     *
     * @throws Exception
     */
    @Test
    public void testLoginPermanentTokenAndLogoutForMultipleUsersScenario1() throws Exception {

        // set full permission
        this.setFullPermission(userIDForTest);
        this.setFullPermission(userIDForTest+1);

        // 1. login user "user1" and get normal token "tokenA"
        String tokenA = this.login(userIDForTest, passwordForTest, false);

        this.validateTokenAvailable(tokenA);

        // 2. login user "user2" and get permanent token "tokenB"
        String tokenB = this.login(userIDForTest+1, passwordForTest, true);

        this.validateTokenAvailable(tokenB);

        // 3. after "user2" logout, "tokenB" is not available, while "tokenA" is still available
        this.logout(tokenB);

        this.validateTokenAvailable(tokenA);
        this.validateTokenUnavailable(tokenB);

    }

    /**
     * this test case is to test no conflict between the normal/permanent tokens from different users
     * need to shorten the token expire time to 15 seconds for below testing
     *
     * 1. login user "user1" and get normal token "tokenA"
     * 2. login user "user1" and get permanent token "tokenB"
     * 3. after 10 seconds, login user "user2" and get normal token "tokenC"
     * 4. login user "user2" and get permanent token "tokenD"
     * 5. after 10 seconds, "tokenA" is not available, while "tokenB", "tokenC" and "tokenD" are still available
     * 6. logout "tokenD", "tokenD" is not available, while "tokenB" and "tokenC" are still available
     *
     * @throws Exception
     */
    @Test
    public void testLoginPermanentTokenAndLogoutForMultipleUsersScenario2() throws Exception {

        // set full permission
        this.setFullPermission(userIDForTest);
        this.setFullPermission(userIDForTest+1);

        // 1. login user "user1" and get normal token "tokenA"
        String tokenA = this.login(userIDForTest, passwordForTest, false);

        this.validateTokenAvailable(tokenA);

        // 2. login user "user1" and get permanent token "tokenB"
        String tokenB = this.login(userIDForTest, passwordForTest, true);

        this.validateTokenAvailable(tokenB);

        // 3. after 10 seconds, login user "user2" and get normal token "tokenC"
        Thread.sleep(10000);

        String tokenC = this.login(userIDForTest+1, passwordForTest, false);

        this.validateTokenAvailable(tokenC);

        // 4. login user "user2" and get permanent token "tokenD"
        String tokenD = this.login(userIDForTest+1, passwordForTest, true);

        this.validateTokenAvailable(tokenD);

        // 5. after 10 seconds, "tokenA" is not available, while "tokenB", "tokenC" and "tokenD" are still available
        Thread.sleep(10000);

        this.validateTokenUnavailable(tokenA);
        this.validateTokenAvailable(tokenB);
        this.validateTokenAvailable(tokenC);
        this.validateTokenAvailable(tokenD);

        // 6. logout "tokenD", "tokenD" is not available, while "tokenB" and "tokenC" are still available
        this.logout(tokenD);

        this.validateTokenUnavailable(tokenA);
        this.validateTokenAvailable(tokenB);
        this.validateTokenAvailable(tokenC);
        this.validateTokenUnavailable(tokenD);

    }

    @Test
    public void testLoginPermanentTokenException() throws Exception {

        // wrong user id
        Map<String, Object> request = new HashMap<>();
        request.put("userID", "wrongUserID");
        request.put("password", passwordForTest);
        request.put("permanentToken", true);

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
        request.put("permanentToken", true);

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
     * this test case is to test the cache works for permanent token
     * need to shorten the token expire time to 15 seconds for below testing
     *
     * 1. login and get permanent token
     * 2. when token is used in some API, it's got from cache
     * 3. after 20 seconds, when the token is used again in some API, it's got from DB
     * 4. when token is used again in some API, it's got from cache
     *
     * (need to check the log to see whether the permanent token is got from cache or DB)
     *
     * @throws Exception
     */
    @Test
    public void testTokenCache() throws Exception {

        this.setFullPermission(userIDForTest);

        // 1. login and get permanent token
        String token = this.login(userIDForTest, passwordForTest, true);

        System.out.println("$$$$$$$$$$$$$ I'm a unique line indicating the end of '1. login and get permanent token'");

        // 2. when token is used in some API, it's got from cache
        this.validateTokenAvailable(token);

        System.out.println("%%%%%%%%%%%%% I'm a unique line indicating the end of '2. when token is used in some API, it's got from cache'");

        // 3. after 20 seconds, when the token is used again in some API, it's got from DB
        Thread.sleep(20000);

        this.validateTokenAvailable(token);

        System.out.println("@@@@@@@@@@@@@ I'm a unique line indicating the end of '3. after 20 seconds, when the token is used again in some API, it's got from DB'");

        // 4. when token is used again in some API, it's got from cache
        this.validateTokenAvailable(token);

        System.out.println("~~~~~~~~~~~~~ I'm a unique line indicating the end of '4. when token is used again in some API, it's got from cache'");

    }


    /**
     * this case is to test all tokens are not available after change password
     * 1. login and get normal token "tokenA"
     * 2. login and get permanent token "tokenB"
     * 3. after change password, both "tokenA" and "tokenB" are not available
     *
     * @throws Exception
     */
    @Test
    public void testChangePassword() throws Exception {

        this.setFullPermission(userIDForTest);

        // 1. login and get normal token "tokenA"
        String tokenA = this.login(userIDForTest, passwordForTest, false);

        this.validateTokenAvailable(tokenA);

        // login and get permanent token "tokenB"
        String tokenB = this.login(userIDForTest, passwordForTest, true);

        this.validateTokenAvailable(tokenB);

        // 3. after change password, both "tokenA" and "tokenB" are not available
        Map<String, Object> request = new HashMap<>();
        request.put("oldPassword", passwordForTest);
        request.put("newPassword", passwordForTest + "new");

        String ctx= mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/oauth2/changepassword").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(AUTH_HEADER, "Bearer " + tokenA)
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

        this.validateTokenUnavailable(tokenA);
        this.validateTokenUnavailable(tokenB);

    }

}

