package com.kii.beehive.portal.web.controller;


import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.beehive.portal.web.entity.DeviceSupplierRestBean;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/devicesuppliers", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DeviceSupplierController {

	@Autowired
	private DeviceSupplierDao deviceSupplierDao;

	/**
	 * 查询设备供应商信息
	 * 没有相应Kii app信息的供应商不会被返回(实际上不应出现这种情况)
	 * <p>
	 * GET /devicesuppliers/all
	 *
	 * @return
	 */
	@RequestMapping(value = "/all", method = {RequestMethod.GET}, consumes = {"*"})
	public List<DeviceSupplierRestBean> getAllDeviceSuppliers() {

		// get list of device suppliers
		List<DeviceSupplier> list = deviceSupplierDao.getAll();

		List<DeviceSupplierRestBean> restBeanList = new ArrayList<>();

		// fill the kii app id into device supplier
		for (DeviceSupplier deviceSupplier : list) {

			String appName = deviceSupplier.getRelationAppName();
			String appID = deviceSupplier.getRelationAppID();

			// the device supplier without Kii app info will be skipped
			if (Strings.isBlank(appName) || Strings.isBlank(appID)) {
				continue;
			}

			// add device supplier into response list
			DeviceSupplierRestBean restBean = new DeviceSupplierRestBean(deviceSupplier);
			restBeanList.add(restBean);
		}

		return restBeanList;
	}

}
