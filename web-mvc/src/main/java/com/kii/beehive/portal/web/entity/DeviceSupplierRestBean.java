package com.kii.beehive.portal.web.entity;

import org.springframework.beans.BeanUtils;

import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.store.entity.DeviceSupplier;

/**
 * Created by USER on 3/24/16.
 */
public class DeviceSupplierRestBean extends DeviceSupplier {

    public DeviceSupplierRestBean(DeviceSupplier deviceSupplier){
        BeanUtils.copyProperties(deviceSupplier, this);
    }
}
