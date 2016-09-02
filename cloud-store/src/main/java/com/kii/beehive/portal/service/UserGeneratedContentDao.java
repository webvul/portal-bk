package com.kii.beehive.portal.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.store.entity.UserGeneratedContent;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName = "portal", appBindSource = "propAppBindTool")
@Component
public class UserGeneratedContentDao extends AbstractDataAccess<UserGeneratedContent> {


	@Override
	protected Class<UserGeneratedContent> getTypeCls() {
		return UserGeneratedContent.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("UserGeneratedContent");
	}



	public void deleteUserData(Long userID,String type,String name){

		String uuid=UserGeneratedContent.getUUID(userID,type,name);
		super.removeEntity(uuid);

	}


	public void setUserData(UserGeneratedContent content,String uuid) {

		super.updateEntityAll(content,uuid);

	}

//	public String addUserData(Long userID,String type,CustomData data) {
//
//		UserGeneratedContent content=new UserGeneratedContent();
//		content.setUserData(data);
//		content.setUserDataType(type);
//		content.setUserID(userID);
//
//		return super.addEntity(content).getObjectID();
//
//	}

	public CustomData getUserData(String type, String name ,Long userID) {

			String uuid=UserGeneratedContent.getUUID(userID,type,name);

			UserGeneratedContent userData = super.getObjectByID(uuid);
			if(userData!=null) {
				return userData.getUserData();
			}else{
				return new CustomData();
			}
	}

	public List<UserGeneratedContent>  getAllUserData(Long userID, String type){
		QueryParam  query= ConditionBuilder.andCondition().equal("userID",userID).equal("userDataType",type).getFinalQueryParam();

		return super.fullQuery(query);
	}




}
