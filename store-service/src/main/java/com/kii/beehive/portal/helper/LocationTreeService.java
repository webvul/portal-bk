package com.kii.beehive.portal.helper;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.LocationDao;
import com.kii.beehive.portal.store.entity.LocationTree;


@Component
public class LocationTreeService {
	
	@Autowired
	private LocationDao locDao;
	
	
	private AtomicReference<LocationTree> treeCache=new AtomicReference<>();
	
	public void refreshTree(){
		
		treeCache.set(locDao.getFullLocationTree());
		
	}
	
	public LocationTree getLocationTree(){
		
		return treeCache.get();
	}
}
