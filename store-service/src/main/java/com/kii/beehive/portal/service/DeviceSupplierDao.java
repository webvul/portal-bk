package com.kii.beehive.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.config.CacheConfig;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class DeviceSupplierDao extends AbstractDataAccess<DeviceSupplier>{



	@Cacheable(cacheNames = CacheConfig.LONGLIVE_CACHE , key="all_supplier")
	public  Map<String,String> getUrlMap(){
		Map<String,String> urlMap=new HashMap<>();


		List<DeviceSupplier> supplierList=super.getAll();

		supplierList.forEach((s) -> urlMap.put(s.getId(), s.getUserInfoNotifyUrl()));

		return urlMap;
	}

	@CacheEvict(cacheNames = CacheConfig.LONGLIVE_CACHE , key="all_supplier")
	public String addDeviceSupplier(DeviceSupplier entity){
		return  super.addKiiEntity(entity);
	}

	@CacheEvict(cacheNames = CacheConfig.LONGLIVE_CACHE , key="all_supplier")
	public void updateSupplier(DeviceSupplier supplier){

		super.updateEntity(supplier, supplier.getId());
	}

	@CacheEvict(cacheNames = CacheConfig.LONGLIVE_CACHE , key="all_supplier")
	public void removeDeviceSupplier(String party3rdID){

		super.removeEntity(party3rdID);
	}

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
