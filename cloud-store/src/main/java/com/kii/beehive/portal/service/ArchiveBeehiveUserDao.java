package com.kii.beehive.portal.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.PortalSyncUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind = TokenBindTool.BindType.Custom,customBindName = PortalTokenBindTool.PORTAL_OPER )
@Component
public class ArchiveBeehiveUserDao extends AbstractDataAccess<PortalSyncUser> {

	public void archive(PortalSyncUser user){

		super.addEntity(user, user.getId());


	}

	public PortalSyncUser queryInArchive(PortalSyncUser user){

		QueryParam param= ConditionBuilder.newCondition().equal("userID",user.getAliUserID()).getFinalQueryParam();


		List<PortalSyncUser> list=super.query(param);

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
	protected Class<PortalSyncUser> getTypeCls() {
		return PortalSyncUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("archiveBeehiveUser");
	}

}
