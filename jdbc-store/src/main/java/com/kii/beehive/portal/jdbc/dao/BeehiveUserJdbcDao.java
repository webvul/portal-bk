package com.kii.beehive.portal.jdbc.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;


@Repository
public class BeehiveUserJdbcDao extends SpringBaseDao<BeehiveJdbcUser>  {


	@Override
	protected String getTableName() {
		return "beehive_user";
	}

	@Override
	protected String getKey() {
		return "user_id";
	}



	public List<BeehiveJdbcUser> getUserByIDs(Collection<Long> userIDList) {

		return super.findByIDs(userIDList);
	}

	public BeehiveJdbcUser getUserByID(Long userID) {

		return super.findByID(userID);

	}

	public BeehiveJdbcUser getUserByName(String userName){

		String sql="select * from "
		QueryParam  query= ConditionBuilder.orCondition().equal("userName",userName).equal("phone",userName).equal("mail",userName).getFinalQueryParam();

		List<BeehiveUser>  userList= super.query(query);

		if(userList.isEmpty()){
			return null;
		}else if(userList.size()>1){
			throw new IllegalArgumentException("user more than one");
		}else{
			return userList.get(0);
		}
	}

	/**
	 * 查询用户是否注册过
	 * userName、phone、mail 任意一个都可以作为登录名，所以不能重复
	 * @param user
	 * @return
	 */
	public BeehiveJdbcUser getUserByLoginId(BeehiveUser user){
		ConditionBuilder conditionBuilder = ConditionBuilder.orCondition().equal("userName",user.getUserName());
		if(StringUtils.hasText(user.getPhone())){
			conditionBuilder.equal("phone",user.getPhone());
		}
		if(StringUtils.hasText(user.getMail())){
			conditionBuilder.equal("mail",user.getMail());
		}
		List<BeehiveUser>  userList= super.query(conditionBuilder.getFinalQueryParam());

		if(userList.isEmpty()){
			return null;
		}else if(userList.size()>1){
			throw new IllegalArgumentException("user more than one");
		}else{
			return userList.get(0);
		}
	}

	public void deleteUser(String userID) {

		super.removeEntity(userID);

	}


	public void setPassword(String id, String pwd) {

		Map<String,Object> params=new HashMap<>();
		params.put("activityToken",null);
		params.put("userPassword",pwd);

		super.updateEntity(params,id);
	}

	public List<BeehiveJdbcUser> getAllUsers() {
		return super.getAll();
	}

	public List<BeehiveJdbcUser> getUsersBySimpleQuery(Map<String, Object> params) {
		QueryParam query = getEntitysByFields(params);

		return super.fullQuery(query);
	}


}
