package com.kii.beehive.portal.store.repositories;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;

@Component
public interface ThingInfoRepository extends CrudRepository<GlobalThingInfo, String> {


	GlobalThingInfo findByVendorThingID(String macAddress);


	Set<GlobalThingInfo>  queryByLocationID(String location);


}
