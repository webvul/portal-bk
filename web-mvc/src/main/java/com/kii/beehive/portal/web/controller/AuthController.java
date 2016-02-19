package com.kii.beehive.portal.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.manager.UserManager;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.entity.AuthRestBean;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.sdk.entity.LoginInfo;

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
    
    @Autowired
    private TeamDao teamDao;
    
    @Autowired
	private TeamUserRelationDao teamUserRelationDao;


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
        String teamName = (String)request.get("teamName");

        if(CollectUtils.containsBlank(userID, password)) {
            throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userID or password empty", HttpStatus.BAD_REQUEST);
        }
        
        boolean result = authManager.register(userID, password);

        if(result == false) {
            throw new PortalException(ErrorCode.AUTH_FAIL, "userID incorrect or already registered", HttpStatus.BAD_REQUEST);
        }
        
        //create team
        if(!Strings.isBlank(teamName)){
        	List<Team> teamList = teamDao.findTeamByTeamName(teamName);
        	Long teamID = null;
        	if(teamList.size() == 0){//create team and user add to team
        		Team t = new Team();
            	t.setName(teamName);
            	teamID = teamDao.saveOrUpdate(t);
            	TeamUserRelation tur = new TeamUserRelation(userID, teamID, 1);
            	teamUserRelationDao.saveOrUpdate(tur);
            	
            	//first user add to admin userGroup
            	UserGroup userGroup = new UserGroup();
            	userGroup.setName(Constants.ADMIN_GROUP);
            	userManager.createUserGroup(userGroup,userID);
            	
        	}else{// user add to team
        		teamID = teamList.get(0).getId();
        		TeamUserRelation tur = new TeamUserRelation(userID, teamID, 0);
            	teamUserRelationDao.saveOrUpdate(tur);
        	}
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

        if(CollectUtils.containsBlank(userID, password)) {
            throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userID or password empty", HttpStatus.BAD_REQUEST);
        }

        LoginInfo loginInfo = authManager.login(userID, password);

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
    public void login(HttpServletRequest request) {

        String auth = request.getHeader(Constants.ACCESS_TOKEN);

        if (auth == null || !auth.startsWith("Bearer ")) {
            return;
        }

        auth = auth.trim();

        String token = auth.substring(auth.indexOf(" ") + 1).trim();

        authManager.logout(token);

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
