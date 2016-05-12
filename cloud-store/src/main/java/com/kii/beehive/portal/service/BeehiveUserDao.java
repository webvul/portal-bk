package com.kii.beehive.portal.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class BeehiveUserDao extends AbstractDataAccess<BeehiveUser> {



	public List<BeehiveUser> getUserByIDs(Collection<String> userIDList) {

		return super.getEntitys(userIDList.toArray(new String[0]));
	}
	
	public BeehiveUser getUserByID(String userID) {

		return super.getObjectByID(userID);

	}

	public BeehiveUser getUserByName(String userName){
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
	public BeehiveUser getUserByLoginId(BeehiveUser user){
		QueryParam  query= ConditionBuilder.orCondition().equal("userName",user.getUserName()).equal("phone",user.getPhone())
				.equal("mail",user.getMail()).getFinalQueryParam();

		List<BeehiveUser>  userList= super.query(query);

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

		Map<String,Object>  params=new HashMap<>();
		params.put("activityToken",null);
		params.put("userPassword",pwd);

		super.updateEntity(params,id);
	}

	public List<BeehiveUser> getAllUsers() {
		return super.getAll();
	}

	public List<BeehiveUser> getUsersBySimpleQuery(Map<String, Object> params) {
		QueryParam query = getEntitysByFields(params);

		return super.fullQuery(query);
	}


	public QueryParam getEntitysByFields(Map<String, Object> fields) {

		ConditionBuilder builder = ConditionBuilder.andCondition();

		fields.forEach((k, v) -> {
			builder.equal(k, v);
		});

		return builder.getFinalCondition().build();

	}

	@Override
	protected Class<BeehiveUser> getTypeCls() {
		return BeehiveUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehiveUser");
	}
	

}
