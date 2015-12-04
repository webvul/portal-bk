package com.kii.beehive.portal.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class ArchiveBeehiveUserDao extends AbstractDataAccess<BeehiveUser> {

	public void archive(BeehiveUser user){

		super.addEntity(user, user.getId());


	}

	public BeehiveUser queryInArchive(BeehiveUser user){

		QueryParam param= ConditionBuilder.orCondition().equal("userName",user.getUserName()).equal("aliUserID",user.getAliUserID()).getFinalQueryParam();


		List<BeehiveUser> list=super.query(param);

		if(list.size()==0){
			return null;
		}else{
			return list.get(0);
		}


	}

	public void removeArchive(String userID){
		super.removeEntity(userID);
	}

	@Override
	protected Class<BeehiveUser> getTypeCls() {
		return BeehiveUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("archiveBeehiveUser");
	}

}
