package com.kii.beehive.portal.store.entity;

import com.kii.extension.ruleengine.sdk.entity.KiiEntity;

public class PortalEntity extends KiiEntity {


	private String createBy;

	private String modifyBy;

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}
}
