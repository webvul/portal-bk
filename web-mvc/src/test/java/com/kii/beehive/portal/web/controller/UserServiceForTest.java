package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.service.UserService;

/**
 * Created by USER on 12/27/15.
 */
@Component
@BindAppByName(appName="master")
public class UserServiceForTest {

    @Autowired
    private TokenBindToolResolver tokenBindToolResolver;

    @Autowired
    private UserService userService;

    /**
     * return kii user id
     * @param kiiUser
     * @return
     */
    public String createUser(KiiUser kiiUser) {
        return userService.createUser(kiiUser);
    }

    public void removeUser(String userID) {
        tokenBindToolResolver.bindByType(TokenBindTool.BindType.admin.name());
        userService.removeUserByLoginName(userID);
        tokenBindToolResolver.clean();
    }

}
