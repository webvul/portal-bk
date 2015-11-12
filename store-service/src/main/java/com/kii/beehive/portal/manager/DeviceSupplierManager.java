package com.kii.beehive.portal.manager;

import javax.annotation.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.store.entity.DeviceSupplier;

@Component
public class DeviceSupplierManager {


	private Map<String,DeviceSupplier> deviceInfoMap=new ConcurrentHashMap<>();

	@Autowired
	private DeviceSupplierDao  supplierDao;

	@PostConstruct
	public void init(){

		supplierDao.getAllSupplier().stream().forEach((entity) -> {
			deviceInfoMap.put(entity.getId(), entity);
		});

	}


	public String addSupplier(DeviceSupplier supplier){

		String id=supplierDao.addDeviceSupplier(supplier);

		deviceInfoMap.put(id, supplier);

		return id;
	}

	public void removeSupplier(String id){
		deviceInfoMap.remove(id);

		supplierDao.removeDeviceSupplier(id);
	}

	public DeviceSupplier getDeviceSupplierByID(String id){

		DeviceSupplier supplier=deviceInfoMap.getOrDefault(id,supplierDao.getSupplierByID(id));

		if(supplier!=null){
			deviceInfoMap.put(id,supplier);
		}
		return supplier;
	}

	public void updateNotifyUrl(String id,String url){

		DeviceSupplier supplier=new DeviceSupplier();
		supplier.setUserInfoNotifyUrl(url);

		supplierDao.updateSupplier(id,supplier);
	}



}
