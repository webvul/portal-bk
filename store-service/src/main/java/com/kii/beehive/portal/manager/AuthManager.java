package com.kii.beehive.portal.manager;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.helper.AuthInfoCacheService;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.UserTokenBindTool;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.exception.KiiCloudException;
import com.kii.extension.sdk.service.UserService;

@BindAppByName(appName="master")
@Component
public class AuthManager {

    private Logger log= LoggerFactory.getLogger(AuthManager.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthInfoCacheService authInfoCacheService;

    @Autowired
    private UserTokenBindTool userTokenBindTool;
    
    /**
     * register with userID and password
     * the logic behind is as below:
     * 1. login with the default password (generated by KiiUserSyncDao when created beehive user)
     * 2. change password with the token from login
     *
     * @param userID
     * @param password
     */
    public boolean register(String userID, String password) {

        try {
            String defaultPassword = this.getDefaultPassword(userID);

            // login Kii Cloud
            LoginInfo loginInfo = userService.login(userID, defaultPassword);

            // bind token to ThreadLocal
            userTokenBindTool.bindToken(loginInfo.getToken());

            // change from default password to new password
            userService.changePassword(defaultPassword, password);

        } catch (KiiCloudException e) {
            log.debug("Login with default password failed", e);
            return false;
        }

        return true;
    }

    private String getDefaultPassword(String userID) {
        return DigestUtils.sha1Hex(userID+"_beehive");
    }

    /**
     * login Kii Cloud and save the token info into DB
     * @param userID
     * @param password
     * @return
     */
    public LoginInfo login(String userID, String password) {

        // login Kii Cloud
        LoginInfo loginInfo = null;

        try {
            loginInfo = userService.login(userID, password);
        } catch(KiiCloudException e) {
            log.debug("Login failed", e);
            return null;
        }

        // insert or update the auth info into DB and cache, the expire time doesn't follow LoginInfo
        authInfoCacheService.saveToken(userID, loginInfo.getToken());

        return loginInfo;
    }
    
    public void saveToken(String userID, String token){
    	authInfoCacheService.saveToken(userID, token);
    }

    /**
     * change the Kii user password in Kii Cloud;
     * if success, remove the token
     *
     * @param oldPassword
     * @param newPassword
     */
    public void changePassword(String oldPassword, String newPassword) {

        userService.changePassword(oldPassword, newPassword);

        String token = userTokenBindTool.getToken();
        authInfoCacheService.removeToken(token);

    }

    /**
     * validate user token,
     * if token is valid, set it into ThreadLocal
     * @param token
     * @return
     */
    public AuthInfo validateAndBindUserToken(String token) {

        AuthInfo authInfo = authInfoCacheService.getAvailableAuthInfo(token);

        if(authInfo == null) {
            return null;
        }
        
        userTokenBindTool.bindToken(authInfo.getToken());

        return authInfo;
    }

    /**
     * remove the token
     * @param token
     */
    public void logout(String token) {

        authInfoCacheService.removeToken(token);
    }

    /**
     * clean the user token in ThreadLocal
     */
    public void unbindUserToken() {
        userTokenBindTool.clean();
    }

    /**
     * get AuthInfo by token directly <br/>
     * this method will not check whether token is valid, so is supposed only to be called after AuthInterceptor validated the token
     *
     * @param token
     * @return
     */
    public AuthInfo getAuthInfo(String token) {
    	if(Strings.isBlank(token)){
    		return null;
    	}
    	
        return authInfoCacheService.getAuthInfo(token);
    }

}

