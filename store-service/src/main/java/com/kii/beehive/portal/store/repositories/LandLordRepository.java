package com.kii.beehive.portal.store.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.LandLord;

@Component
public interface LandLordRepository extends CrudRepository<LandLord, String> {



}
