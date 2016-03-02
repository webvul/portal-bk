package com.kii.beehive.portal.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.manager.UserManager;
import com.kii.beehive.portal.store.entity.AuthInfoEntry;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.entity.AuthRestBean;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.exception.UnauthorizedAccessException;

/**
 * Beehive API - User API
 *
 * refer to doc "Beehive API - Tech Design" section "User API" for details
 */
@RestController
@RequestMapping(path = "/oauth2", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
        MediaType.APPLICATION_JSON_UTF8_VALUE })
public class AuthController {

	@Autowired
    private AuthManager authManager;

    @Autowired
    private UserManager userManager;
    
    /**
     * 用户注册
     * POST /oauth2/register
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @return
     */
    @RequestMapping(path = "/register", method = { RequestMethod.POST })
    public void register(@RequestBody Map<String, Object> request) {

        String userID = (String)request.get("userID");
        String password = (String)request.get("password");

        if(CollectUtils.containsBlank(userID, password)) {
            throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userID or password empty", HttpStatus.BAD_REQUEST);
        }
        
        boolean result = authManager.register(userID, password);

        if(result == false) {
            throw new PortalException(ErrorCode.AUTH_FAIL, "userID incorrect or already registered", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 用户登录
     * POST /oauth2/login
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @return
     */
    @RequestMapping(path = "/login", method = { RequestMethod.POST })
    public AuthRestBean login(@RequestBody Map<String, Object> request) {

        String userID = (String)request.get("userID");
        String password = (String)request.get("password");
        Boolean permanentToken = (Boolean)request.get("permanentToken");
        // if permanentToken is not set, make it false as default
        if(permanentToken == null) {
            permanentToken = false;
        }

        if(CollectUtils.containsBlank(userID, password)) {
            throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userID or password empty", HttpStatus.BAD_REQUEST);
        }

        LoginInfo loginInfo = authManager.login(userID, password, permanentToken);

        if(loginInfo == null) {
            throw new PortalException(ErrorCode.AUTH_FAIL, "Authentication failed", HttpStatus.BAD_REQUEST);
        }
        
        // get user info
        BeehiveUser beehiveUser = userManager.getUserByID(userID);
        
        AuthRestBean authRestBean = new AuthRestBean(beehiveUser);
        
        Team team = userManager.getTeamByID(userID);
        if(team != null){
        	authRestBean.setTeamID(team.getId());
        	authRestBean.setTeamName(team.getName());
        	AuthInfoStore.setTeamID(team.getId());
        }

        // get access token
        String accessToken = loginInfo.getToken();
        authRestBean.setAccessToken(accessToken);
        authRestBean.setPermissions(loginInfo.getPermissionSet());
        return authRestBean;
    }

    /**
     * 用户登出
     * POST /oauth2/logout
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @return
     */
    @RequestMapping(path = "/logout", method = { RequestMethod.POST })
    public void logout(HttpServletRequest request) {

        String token = getTokenFromHttpHeader(request);

        if(token == null) {
            return;
        }

        authManager.logout(token);

    }

    private String getTokenFromHttpHeader(HttpServletRequest request) {
        String auth = request.getHeader(Constants.ACCESS_TOKEN);

        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }

        auth = auth.trim();

        String token = auth.substring(auth.indexOf(" ") + 1).trim();

        return token;
    }

    /**
     * 验证用户（令牌）
     * POST /oauth2/validatetoken
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @return
     */
    @RequestMapping(path = "/validatetoken", method = { RequestMethod.POST })
    public AuthRestBean validateUserToken(HttpServletRequest request) {

        String token = getTokenFromHttpHeader(request);
        AuthInfoEntry entry = authManager.getAuthInfoEntry(token);

        // this case would rarely happen, because token is validated in AuthInterceptor before coming here
        if(entry == null) {
            throw new PortalException(ErrorCode.AUTH_FAIL, "Authentication failed", HttpStatus.BAD_REQUEST);
        }

        String userID = entry.getUserID();

        // get user info
        BeehiveUser beehiveUser = userManager.getUserByID(userID);

        AuthRestBean authRestBean = new AuthRestBean(beehiveUser);

        Team team = userManager.getTeamByID(userID);
        if(team != null){
            authRestBean.setTeamID(team.getId());
            authRestBean.setTeamName(team.getName());
        }

        // get access token
        String accessToken = entry.getToken();
        authRestBean.setAccessToken(accessToken);
        authRestBean.setPermissions(entry.getPermissionSet());
        return authRestBean;
    }


    /**
     * 用户修改密码
     * POST /oauth2/changepassword
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @return
     */
    @RequestMapping(path = "/changepassword", method = { RequestMethod.POST })
    public void changePassword(@RequestBody Map<String, Object> request) {

        String oldPassword = (String)request.get("oldPassword");
        String newPassord = (String)request.get("newPassword");

        if(CollectUtils.containsBlank(oldPassword, newPassord)) {
            throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "oldPassword or newPassord empty", HttpStatus.BAD_REQUEST);
        }

        authManager.changePassword(oldPassword, newPassord);

    }


}
