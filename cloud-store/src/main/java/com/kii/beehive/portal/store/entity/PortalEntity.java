package com.kii.beehive.portal.store.entity;

import com.kii.extension.sdk.entity.KiiEntity;

public class PortalEntity extends KiiEntity {
	
	
	public enum EntityStatus{
		enable,disable,deleted;
		
		
		public static EntityStatus getInst(boolean sign){
			return sign? enable: disable;
		}
	}
	
	
	private EntityStatus status=EntityStatus.enable;
	
	public EntityStatus getStatus() {
		return status;
	}
	
	public void setStatus(EntityStatus status) {
		this.status = status;
	}
	
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
