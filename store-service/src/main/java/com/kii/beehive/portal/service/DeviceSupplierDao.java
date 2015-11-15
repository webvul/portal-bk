package com.kii.beehive.portal.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
@Component
public class DeviceSupplierDao extends AbstractDataAccess<DeviceSupplier>{

	public List<DeviceSupplier> getAllSupplier(){
		return super.getAll();
	}

	public String addDeviceSupplier(DeviceSupplier entity){
		return  super.addKiiEntity(entity);
	}

	public void updateSupplier(String party3rdID, DeviceSupplier map){

		super.updateEntity(map, party3rdID);
	}

	public void removeDeviceSupplier(String party3rdID){

		super.removeEntity(party3rdID);
	}


	@Override
	protected Class<DeviceSupplier> getTypeCls() {
		return DeviceSupplier.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("DeviceSupplier");
	}

	public DeviceSupplier getSupplierByID(String id) {
		return super.getObjectByID(id);
	}
}
