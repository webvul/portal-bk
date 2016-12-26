//package com.kii.beehive.portal.service;
//
//import java.util.List;
//
//import org.springframework.stereotype.Component;
//
//import com.kii.beehive.portal.store.entity.BeehiveGuestLog;
//import com.kii.extension.sdk.annotation.BindAppByName;
//import com.kii.extension.sdk.entity.BucketInfo;
//
//@BindAppByName(appName="portal",appBindSource="propAppBindTool")
//@Component
//public class BeehiveGuestDao extends BaseKiicloudDao<Beegh> {
//
//
//	public void addGuest(BeehiveGuestLog  guest){
//
//		super.addKiiEntity(guest);
//	}
//
//	public List<BeehiveGuestLog> queryGuest(){
//
//		return null;
//	}
//
//
//	@Override
//	protected Class getTypeCls() {
//		return BeehiveGuestLog.class;
//	}
//
//	@Override
//	protected BucketInfo getBucketInfo() {
//		return new BucketInfo("beehiveGuestLog");
//	}
//}
