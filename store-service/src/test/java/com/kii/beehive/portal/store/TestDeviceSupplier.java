package com.kii.beehive.portal.store;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.store.entity.DeviceSupplier;

public class TestDeviceSupplier extends TestInit {

	@Autowired
	private DeviceSupplierDao deviceDao;

	String appName1="test-slave-1";
	String appName2="test-slave-2";

	@Test
	public void testAdd(){


		for(int i=0;i<10;i++) {
			DeviceSupplier entity = new DeviceSupplier();
			entity.setName("name:"+i);

			entity.setRelationAppName(i%2==0?appName1:appName2);

			deviceDao.addDeviceSupplier(entity);
		}


	}


}
