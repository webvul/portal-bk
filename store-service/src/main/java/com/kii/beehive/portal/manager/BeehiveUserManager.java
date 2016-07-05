package com.kii.beehive.portal.manager;

import javax.annotation.PostConstruct;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StringRandomTools;
import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.extend.FacePlusPlusService;
import com.kii.beehive.portal.extend.entitys.FaceUser;
import com.kii.beehive.portal.helper.RuleSetService;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;
import com.kii.beehive.portal.store.entity.BeehiveUser;

@Component
@Transactional
public class BeehiveUserManager {




	@Autowired
	protected TeamUserRelationDao teamUserRelationDao;

	@Autowired
	protected TeamGroupRelationDao teamGroupRelationDao;

	@Autowired
	private KiiUserService kiiUserService;

	@Autowired
	private BeehiveUserJdbcDao userDao;

	@Autowired
	private RuleSetService ruleService;

	@Autowired
	private FacePlusPlusService facePlusPlusService;



	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private GroupUserRelationDao groupUserRelationDao;


	@Autowired
	private TeamDao teamDao;

	@Value("${face.photo.dir}")
	private String facePhotoDir;

	public String getFacePhotoDir() {
		return facePhotoDir;
	}

	@PostConstruct
	public void init() throws JsonProcessingException {
		//init dir
		File facePhotoDirFile = new File(facePhotoDir);
		if (!facePhotoDirFile.exists()) {
			boolean isMkdirs = facePhotoDirFile.mkdirs();
			if (!isMkdirs) {
				throw new RuntimeException("create face++ upload photo dir error ! ");
			}
		}
	}

	public PermissionTree getUsersPermissonTree(String userID){

		return ruleService.getUserPermissionTree(userID);
	}


	public void createAdmin(String adminName,String password){

		BeehiveJdbcUser admin=new BeehiveJdbcUser();

		admin.setUserName(adminName);
		String hashedPwd=admin.getHashedPwd(password);
		admin.setUserPassword(hashedPwd);

		userDao.insert(admin);

		String loginID=kiiUserService.addBeehiveUser(admin,admin.getUserPassword());

		admin.setKiiUserID(loginID);

		userDao.updateEntityAllByID(admin);

	}

	public Map<String,Object>  addUser(BeehiveJdbcUser user,String teamName) {


		BeehiveUser existsUser=userDao.getUserByLoginId(user);

		if(existsUser!=null){
			throw new IllegalArgumentException("the username had existed,please change a loginName or email or phone Number");
		}

		userDao.addKiiEntity(user);

		String loginID=kiiUserService.addBeehiveUser(user,user.getDefaultPassword());

		user.setKiiUserID(loginID);

		String token= StringRandomTools.getRandomStr(6);

		user.setActivityToken(user.getHashedPwd(token));

		userDao.updateEntity(user,user.getId());

		Map<String,Object> result=new HashMap<>();

		if(!StringUtils.isEmpty(teamName)){
			Long teamID=addTeam(teamName,user.getId());
			result.put("teamID",teamID);
		}

		result.put("userID",user.getId());
		result.put("activityToken",token);


		return result;
	}


	private Long addTeam(String teamName,String userID){

			List<Team> teamList = teamDao.findTeamByTeamName(teamName);

			Long teamID=null;
			if(teamList.isEmpty()){
				Team t = new Team();
				t.setName(teamName);
				teamID = teamDao.saveOrUpdate(t);
				TeamUserRelation tur = new TeamUserRelation(teamID, userID, 1);
				teamUserRelationDao.saveOrUpdate(tur);

			}else if(teamList.size()==1){// user add to team
				teamID = teamList.get(0).getId();
				TeamUserRelation tur = new TeamUserRelation(teamID, userID, 0);
				teamUserRelationDao.saveOrUpdate(tur);
			}else{
				throw new IllegalArgumentException();
			}
			return teamID;

	}



	public void deleteUser(Long userID) {
		checkTeam(userID);
		BeehiveJdbcUser user = userDao.getUserByID(userID);

		groupUserRelationDao.delete(userID, null);

		kiiUserService.disableBeehiveUser(user);

		userDao.deleteUser(userID);


	}

	public BeehiveJdbcUser getUserByIDDirectly(Long userID){
		return userDao.getUserByID(userID);
	}



	public BeehiveJdbcUser getUserByID(Long userID) {

		checkTeam(userID);
		return userDao.getUserByID(userID);
	}


	public void updateUser(BeehiveJdbcUser user,String userID){

//		checkTeam(userID);
		userDao.updateEntity(user,userID);


	}

	/**
	 *
	 */
	public BeehiveJdbcUser updateUserWithFace( String userId, Boolean clearOldPhoto, List<File> photoFiles ){
		BeehiveJdbcUser user = userDao.getUserByID(userId);
		if(user == null) {
			throw new RuntimeException("can not find user ! ");
		}

		List<Integer> photoIds = new ArrayList<>();
		List<Map<String, Object>> photoList = facePlusPlusService.buildUploadPhotos(photoFiles);
		for (Map<String, Object> photoMap : photoList) {
			Integer photoId = (Integer) ( (Map<String, Object>)photoMap.get("data") ).get("id");
			if(photoId == null) {
				throw new RuntimeException("upload face++ photo error ! ");
			}
			photoIds.add(photoId);
		}
		if( StringUtils.isEmpty(user.getFaceSubjectId()) ) { // register
			FaceUser faceUser = new FaceUser();
			faceUser.setSubject_type(FaceUser.SUBJECT_TYPE_EMPLOYEE);
			faceUser.setName(user.getUserName());
			faceUser.setPhoto_ids(photoIds);
			Map<String, Object> userMap = facePlusPlusService.buildSubject(faceUser);
			Integer faceSubjectId = (Integer) ( (Map<String, Object>)userMap.get("data") ).get("id");
			if(faceSubjectId == null){
				throw new RuntimeException("register face++ user error ! ");
			}
			//
			user.setFaceSubjectId(faceSubjectId);
			userDao.updateEntity(user,userId);
		}else {// update
//			throw new RuntimeException("user already registered face++! ");
			if(clearOldPhoto){// 丢弃原来的照片

			}else { // 保留原来的 照片
				Map<String, Object> userMap = facePlusPlusService.buildGetSubjectById(user.getFaceSubjectId());
				List<Map<String, Object>> oldPhotos = (List<Map<String, Object>>) ( ((Map<String, Object>)userMap.get("data")).get("photos") );
				for( Map<String, Object> lodPhoto : oldPhotos) {
					photoIds.add( (Integer) lodPhoto.get("id") );
				}
			}

			FaceUser faceUser = new FaceUser();
			faceUser.setId(user.getFaceSubjectId());
			faceUser.setName(user.getUserName());
			faceUser.setPhoto_ids(photoIds);
			facePlusPlusService.buildUpdateSubject(faceUser);
		}

		return user;
	}



	public List<BeehiveJdbcUser> simpleQueryUser(Map<String, Object> queryMap) {

		if (queryMap.isEmpty()) {
			return userDao.getAllUsers();
		} else {
			return userDao.getUsersBySimpleQuery(queryMap);
		}
	}


	private void checkTeam(Long userID){
		if(AuthInfoStore.isTeamIDExist()){
			TeamUserRelation tur = teamUserRelationDao.findByTeamIDAndUserID(AuthInfoStore.getTeamID(), userID);
			if(tur == null){
				throw new UnauthorizedException(UnauthorizedException.NOT_IN_CURR_TEAM);
			}
		}
	}
	



}
