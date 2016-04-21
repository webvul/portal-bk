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
		QueryParam  query= ConditionBuilder.orCondition().equal("_id",userName).equal("userName",userName).equal("phone",userName).equal("mail",userName).getFinalQueryParam();

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




	@Override
	protected Class<BeehiveUser> getTypeCls() {
		return BeehiveUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehiveUser");
	}
	

}
