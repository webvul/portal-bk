package com.kii.beehive.portal.service;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.NotifyUserFailure;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;
import org.springframework.stereotype.Component;

import java.util.List;

@BindAppByName(appName="portal")
@Component
public class NotifyUserFailureDao extends AbstractDataAccess<NotifyUserFailure>{

	public void addNotifyUserFailure(NotifyUserFailure entity){

		super.addEntity(entity);
	}

	/**
	 * @deprecated NotifyUserFailure doesn't have primary key
	 *
	 * @param id
     */
	public void removeNotifyUserFailure(String id){

		super.removeEntity(id);
	}

	public List<NotifyUserFailure> getNotifyUserFailureByParty3rdID(String[] party3rdIDs) {

		return getEntitys("party3rdID", party3rdIDs);
	}

	@Override
	protected Class<NotifyUserFailure> getTypeCls() {
		return NotifyUserFailure.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("notifyUserFailure");
	}

	public NotifyUserFailure getNotifyUserFailureByID(String id) {
		return super.getObjectByID(id);
	}
}
