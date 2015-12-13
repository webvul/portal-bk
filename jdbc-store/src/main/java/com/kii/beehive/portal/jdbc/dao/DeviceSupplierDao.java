package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.DeviceSupplier;

@Repository
public class DeviceSupplierDao extends BaseDao<DeviceSupplier>{


	public static final String TABLE_NAME="'device_supplier'";


	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return DeviceSupplier.PARTY_3RD_ID;
	}


	@Override
	public List<DeviceSupplier> mapToList(List<Map<String, Object>> rows) {
		List<DeviceSupplier> list = new ArrayList<DeviceSupplier>();
		for (Map<String, Object> row : rows) {
			DeviceSupplier deviceSupplier = new DeviceSupplier();
			deviceSupplier.setId((long)row.get(DeviceSupplier.PARTY_3RD_ID));
			deviceSupplier.setName((String)row.get(DeviceSupplier.NAME));
			deviceSupplier.setRelationAppName((String)row.get(DeviceSupplier.RELATION_APP_NAME));
			deviceSupplier.setDescription((String)row.get(DeviceSupplier.DESCRIPTION));
			deviceSupplier.setUserInfoNotifyUrl((String)row.get(DeviceSupplier.USER_INFO_NOTIFY_URL));
			mapToListForDBEntity(deviceSupplier, row);

			list.add(deviceSupplier);
		}
		return list;

	}

}
