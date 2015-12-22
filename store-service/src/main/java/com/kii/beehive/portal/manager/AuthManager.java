package com.kii.beehive.portal.manager;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.service.UserService;

@Component
public class AuthManager {

    @Autowired
    private UserService userService;

    public LoginInfo login(String userName, String password) {

        // TODO need to set the expiration
        LoginInfo loginInfo = userService.login(userName, password);

        return loginInfo;
    }

    public boolean changePassword(String oldPassword, String newPassword) {

        // TODO implement

        return true;
    }


}
