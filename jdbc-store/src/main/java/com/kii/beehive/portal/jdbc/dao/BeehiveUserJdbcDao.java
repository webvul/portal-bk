package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagUserRelation;
import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;


@Repository
public class BeehiveUserJdbcDao extends SpringBaseDao<BeehiveJdbcUser>  {
	
	
	public  static final String TABLE_NAME = "beehive_user";

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return "beehive_user_id";
	}



	public Collection<? extends BeehiveJdbcUser> findUserByTagRelThing(Long thingId) {
		String sqlTmp="select u.* from  ${0} u" +
				" inner join ${1} rel on rel.beehive_user_id = u.beehive_user_id " +
				" inner join ${2} t_rel on rel.tag_id = t_rel.tag_id "+
				" where t_rel.thing_id  = ? ";

		String sql=StrTemplate.gener(sqlTmp,TABLE_NAME,TagUserRelationDao.TABLE_NAME, TagThingRelationDao.TABLE_NAME);

		List<BeehiveJdbcUser> rows = query(sql,thingId);
		return rows;


	}



	public List<BeehiveJdbcUser> findUserByThingID(Long thingId) {
		String sqlTmp="select u.* from  ${0} rel inner join ${1} u on rel.beehive_user_id = u.beehive_user_id where rel.${2}  = ? ";
		String sql=StrTemplate.gener(sqlTmp,ThingUserRelationDao.TABLE_NAME,TABLE_NAME,ThingUserRelation.THING_ID);

		List<BeehiveJdbcUser> rows = query(sql,thingId);
		return rows;
	}




	//from tagUserRelation


	public List<BeehiveJdbcUser> findUserByTagName(String  tagName) {
		String sqlTmp="select u.* from  ${0} rel" +
				" inner join ${1} u on rel.beehive_user_id = u.beehive_user_id " +
				" inner join ${2} t on rel.tag_id = t.tag_id "+
				" where t.${3}  = ? ";
		String sql=StrTemplate.gener(sqlTmp,TagUserRelationDao.TABLE_NAME,TABLE_NAME, TagIndexDao.TABLE_NAME,TagIndex.FULL_TAG_NAME);

		List<BeehiveJdbcUser> rows = query(sql,tagName);
		return rows;
	}


	public List<BeehiveJdbcUser> findUserByTagID(Long tagId) {
		String sqlTmp="select u.* from  ${0} rel inner join ${1} u on rel.beehive_user_id = u.beehive_user_id where rel.${2}  = ? ";
		String sql=StrTemplate.gener(sqlTmp,TagUserRelationDao.TABLE_NAME,TABLE_NAME,TagUserRelation.TAG_ID);

		List<BeehiveJdbcUser> rows = query(sql,tagId);
		return rows;
	}


	public List<BeehiveJdbcUser> findUserByTags(List<Long> tagIds) {
		if (null == tagIds || tagIds.isEmpty()) {
			return new ArrayList<>();
		}
		String sqlTmp="select u.* from  ${0} rel inner join ${1} u on rel.beehive_user_id = u.beehive_user_id where rel.${2}  in  (:tagIds) ";
		String sql=StrTemplate.gener(sqlTmp,TagUserRelationDao.TABLE_NAME,TABLE_NAME, TagUserRelation.TAG_ID);

		Map<String, Object> params = new HashMap();
		params.put("tagIds", tagIds);

		return queryByNamedParam(sql,params);
	}

	//from groupUserRelation

	public List<BeehiveJdbcUser> findUserIDByUserGroupID(Long userGroupID) {

		String sqlTmp="select u.* from  ${0} rel inner join ${1} u on rel.beehive_user_id = u.beehive_user_id where rel.${2}  = ? ";
		String sql=StrTemplate.gener(sqlTmp,GroupUserRelationDao.TABLE_NAME,TABLE_NAME,GroupUserRelation.USER_GROUP_ID);

		List<BeehiveJdbcUser> rows = query(sql,userGroupID);
		return rows;
	}


	public List<BeehiveJdbcUser> findUsersByGroups(Collection<Long> userGroupIds) {
		if (null == userGroupIds || userGroupIds.isEmpty()) {
			return new ArrayList<>();
		}

		String sqlTmp="select u.* from  ${0} rel inner join ${1} u on rel.beehive_user_id = u.beehive_user_id where rel.${2}  in  (:groupIds) ";
		String sql=StrTemplate.gener(sqlTmp,GroupUserRelationDao.TABLE_NAME,TABLE_NAME,GroupUserRelation.USER_GROUP_ID);

		Map<String, Object> params = new HashMap();
		params.put("groupIds", userGroupIds);
		return queryByNamedParam(sql, params);
	}


	public List<BeehiveJdbcUser> getUserByIDs(Collection<Long> userIDList) {

		return super.findByIDs(userIDList);
	}

	public BeehiveJdbcUser getUserByID(Long userID) {

		return super.findByID(userID);

	}

	public BeehiveJdbcUser getUserByUserID(String  userID) {


		String sqlTmp="select * from ${0} where user_id = ? ";

		String fullSql= StrTemplate.gener(sqlTmp,TABLE_NAME);

		return queryForObject(fullSql, userID);


	}

	public List<BeehiveJdbcUser> getUserByUserIDs(Collection<String>  userIDList) {


		String sqlTmp="select * from ${0} where user_id in (:list)  ";

		String fullSql= StrTemplate.gener(sqlTmp,TABLE_NAME);

		return queryByNamedParam(fullSql,Collections.singletonMap("list",userIDList));


	}

	public BeehiveJdbcUser getUserByName(String userName){

		String sql="select * from ${0} where user_name = :name or  mobile = :name or user_mail = :name ";

		String fullSql= StrTemplate.gener(sql,TABLE_NAME);

		return queryForObjectByNamedParam(fullSql, Collections.singletonMap("name", userName));


	}

	/**
	 * 查询用户是否注册过
	 * userName、phone、mail 任意一个都可以作为登录名，所以不能重复
	 * @param user
	 * @return
	 */
	public BeehiveJdbcUser getUserByLoginId(BeehiveJdbcUser user){


		String sql="select * from ${0} where user_name =  ? ";
		List<Object> params=new ArrayList<>();

		params.add(user.getUserName());

		if(StringUtils.hasText(user.getPhone())){
			sql+="  or mobile =  ? ";
			params.add(user.getPhone());
		}
		if(StringUtils.hasText(user.getMail())){
			sql+= " or user_mail = ? ";
			params.add(user.getMail());
		}

		String fullSql= StrTemplate.gener(sql,TABLE_NAME);


		return queryForObject(fullSql,params.toArray());


	}

	public void deleteUser(Long  userID) {

		super.deleteByID(userID);

	}


	public void setPassword(Long id, String pwd) {

		Map<String,Object> params=new HashMap<>();
		params.put("activityToken",null);
		params.put("userPassword",pwd);

		super.updateEntityByID(params,id);
	}

	public List<BeehiveJdbcUser> getAllUsers() {

		return super.findAll();
	}

	public void updateEnableSign(String userID,boolean sign){

		BeehiveJdbcUser user=new BeehiveJdbcUser();
		user.setUserID(userID);
		user.setEnable(sign);

		super.updateEntityByField(user,"user_id");
	}

	public void deleteUserByUserID(String userID) {
		BeehiveJdbcUser user=new BeehiveJdbcUser();
		user.setUserID(userID);
		user.setDeleted(true);

		super.updateEntityByField(user,"user_id");

	}

	public List<BeehiveJdbcUser> getUsersBySimpleQuery(Map<String, Object> params) {


		return super.findByFields(params);
	}


	public BeehiveJdbcUser addUser(BeehiveJdbcUser  user){


		user.setUserPassword("");
		user.setUserID("");

		Long id=super.insert(user);

		user.setId(id);

		if(StringUtils.isEmpty(user.getUserID())) {
			String userID = DigestUtils.sha1Hex(String.valueOf(id)+"_beehive_hash_"+user.getUserName()+"_name");

			user.setUserID(userID);
		}

		return user;

	}
	

	public BeehiveJdbcUser getUserByKiiUserID(String userID) {

		String sql="select * from ${0} where kii_user_id = ?";

		String fullSql=StrTemplate.gener(sql,TABLE_NAME);

		return  queryForObject(fullSql,userID);
	}
	

}
