package com.kii.beehive.portal.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.entity.UserRestBean;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/users",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserController  extends AbstractController{
	
	@Autowired
	private TeamUserRelationDao teamUserRelationDao;
	
	@Autowired
    private TeamDao teamDao;
    
	/**
	 * 创建用户
	 * POST /users
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(path="",method={RequestMethod.POST})
	public Map<String,String> createUser(@RequestBody UserRestBean user){

		user.verifyInput();

		BeehiveUser beehiveUser = user.getBeehiveUser();

		String userID = userManager.addUser(beehiveUser);
		
		//create team
        if(!Strings.isBlank(user.getTeamName())){
        	List<Team> teamList = teamDao.findTeamByTeamName(user.getTeamName());
        	Long teamID = null;
        	if(teamList.size() == 0){//create team and user add to team
        		Team t = new Team();
            	t.setName(user.getTeamName());
            	teamID = teamDao.saveOrUpdate(t);
            	TeamUserRelation tur = new TeamUserRelation(teamID, userID, 1);
            	teamUserRelationDao.saveOrUpdate(tur);
            	
            	//first user add to admin userGroup
            	UserGroup userGroup = new UserGroup();
            	userGroup.setName(Constants.ADMIN_GROUP);
            	Long userGroupID = userManager.createUserGroup(userGroup,userID);
            	
            	userManager.setDefaultPermission(userGroupID);
            	
        	}else{// user add to team
        		teamID = teamList.get(0).getId();
        		TeamUserRelation tur = new TeamUserRelation(teamID, userID, 0);
            	teamUserRelationDao.saveOrUpdate(tur);
        	}
        }

		Map<String,String> map = new HashMap<>();
		map.put("userID", userID);
		map.put("userName", user.getUserName());

		return map;
	}

	/**
	 * 更新用户
	 * PATCH /users/{userID}
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(path="/{userID}",method={RequestMethod.PATCH})
	public Map<String,String> updateUser(@PathVariable("userID") String userID,@RequestBody UserRestBean user){
		
		checkTeam(userID);
		
		// clean the input user id
		user.setAliUserID(null);

		userManager.updateUser(user.getBeehiveUser(),userID);


		Map<String,String> map=new HashMap<>();
		map.put("userID",userID);
		return map;
	}

	/**
	 * 通过userID查询用户
	 * GET /users/{userID}
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(path="/{userID}",method={RequestMethod.GET})
	public UserRestBean getUser(@PathVariable("userID") String userID){
		
		checkTeam(userID);
		
		return new UserRestBean(userManager.getUserByID(userID));
	}

	/**
	 * 更新用户（第三方）
	 * PATCH /users/<userID>/custom
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(path="/{userID}/custom",method={RequestMethod.PATCH})
	public Map<String,String> updateCustomProp(@PathVariable("userID") String userID,@RequestBody Map<String,Object> props){
		
		checkTeam(userID);
		
		userManager.updateCustomProp(userID,props);

		Map<String,String> map=new HashMap<>();
		map.put("userID",userID);
		return map;
	}

	/**
	 * 删除用户
	 * DELETE /users/<userID>
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(path="/{userID}",method={RequestMethod.DELETE},consumes={"*"})
	public void deleteUser(@PathVariable("userID") String userID){
		checkTeam(userID);
		userManager.deleteUser(userID);

	}

	/**
	 * 查询用户
	 * POST /users/simplequery
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param queryMap
	 */
	@RequestMapping(path="/simplequery",method={RequestMethod.POST})
	public List<UserRestBean> queryUserByProps(@RequestBody Map<String,Object> queryMap){
		
		return  userManager.simpleQueryUser(queryMap).stream()
				.map((e) -> new UserRestBean(e))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	@RequestMapping(path="/info",method={RequestMethod.GET})
	public Map<String, String> info(HttpServletRequest httpRequest){
		Map<String, String> map = new HashMap<>();
		InputStream manifestStream = httpRequest.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
		 try {
		        Manifest manifest = new Manifest(manifestStream);
		        Attributes attributes = manifest.getMainAttributes();
		        String impVersion = attributes.getValue("Implementation-Version");
		        String impTitle = attributes.getValue("Implementation-Title");
		        String impTimestamp = attributes.getValue("Implementation-Timestamp");
				map.put("Version", impVersion);
				map.put("Title", impTitle);
				map.put("Date", impTimestamp);
		    }catch(IOException ex) {
		        //log.warn("Error while reading version: " + ex.getMessage());
		    }
		return  map;
	}
	
	private void checkTeam(String userID){
		if(isTeamIDExist()){
			TeamUserRelation tur = teamUserRelationDao.findByTeamIDAndUserID(this.getLoginTeamID(), userID);
			if(tur == null){
				throw new PortalException("User Not Found", "userID:" + userID + " Not Found", HttpStatus.NOT_FOUND);
			}
		}
	}
}
