package com.kii.beehive.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
@Component
public class DeviceSupplierDao extends AbstractDataAccess<DeviceSupplier>{

//	@Cacheable(cacheNames = "device-supplier",key="all")
//	public List<DeviceSupplier> getAllSupplier(){
//		return super.getAll();
//	}

	@Cacheable(cacheNames = "device-supplier" , key="'all-url'")
	public  Map<String,String> getUrlMap(){
		Map<String,String> urlMap=new HashMap<>();


		List<DeviceSupplier> supplierList=super.getAll();

		supplierList.forEach((s) -> urlMap.put(s.getId(), s.getUserInfoNotifyUrl()));

		return urlMap;
	}

	@CacheEvict(cacheNames="device-supplier",key="'all-url'")
	@CachePut(cacheNames="device-supplier",key="#entity.id")
	public String addDeviceSupplier(DeviceSupplier entity){
		return  super.addKiiEntity(entity);
	}

	@CacheEvict(cacheNames="device-supplier",key="'all-url'")
	@CachePut(cacheNames="device-supplier",key="#entity.id")
	public void updateSupplier(DeviceSupplier supplier){

		super.updateEntity(supplier, supplier.getId());
	}

	@CacheEvict(cacheNames="device-supplier",allEntries=true)
	public void removeDeviceSupplier(String party3rdID){

		super.removeEntity(party3rdID);
	}

	@Cacheable(cacheNames="device-supplier")
	public DeviceSupplier getSupplierByID(String id) {
		return super.getObjectByID(id);
	}




	@Override
	protected Class<DeviceSupplier> getTypeCls() {
		return DeviceSupplier.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("DeviceSupplier");
	}


}
