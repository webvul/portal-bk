package com.kii.beehive.portal.service;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	public void updateSupplier(String id,DeviceSupplier map){

		super.updateEntity(map,id);
	}

	public void removeDeviceSupplier(String id){

		super.removeEntity(id);
	}





	@Override
	protected Class<DeviceSupplier> getTypeCls() {
		return DeviceSupplier.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("deviceParty3rd");
	}

	public DeviceSupplier getSupplierByID(String id) {
		return super.getObjectByID(id);
	}
}
