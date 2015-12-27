package com.kii.beehive.portal.manager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.helper.AuthInfoCacheService;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.UserTokenBindTool;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.exception.UserNotFoundException;
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
        } catch(UserNotFoundException e) {
            log.debug("Login failed", e);
            return null;
        }

        // insert or update the auth info into DB and cache, the expire time doesn't follow LoginInfo
        authInfoCacheService.saveToken(userID, loginInfo.getToken());

        return loginInfo;
    }

    /**
     * change the Kii user password in Kii Cloud
     * @param oldPassword
     * @param newPassword
     */
    public void changePassword(String oldPassword, String newPassword) {

        userService.changePassword(oldPassword, newPassword);

    }

    /**
     * validate token,
     * if token is valid, set it into ThreadLocal
     * @param token
     * @return
     */
    public boolean validateToken(String token) {

        AuthInfo authInfo = authInfoCacheService.getAvailableAuthInfo(token);

        if(authInfo == null) {
            return false;
        }

        userTokenBindTool.bindToken(authInfo.getToken());

        return true;
    }

    /**
     * remove the token
     * @param token
     */
    public void logout(String token) {

        authInfoCacheService.removeToken(token);
    }


}

