package com.kii.beehive.business.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.elasticsearch.BulkOperate;
import com.kii.beehive.business.elasticsearch.ESIndex;
import com.kii.beehive.business.elasticsearch.ESService;
import com.kii.beehive.business.entity.ESThingStatus;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class ESThingStatusService {
	
	private Logger log= LoggerFactory.getLogger(ESThingStatusService.class);
	
	@Autowired
	private ThingTagManager manager;
	
	@Autowired
	private ESService service;

	
	
	@Scheduled(fixedRate = 1000*60*60)
	public void updateThingEntitys(){
		
		manager.getAllThingFullInfo().forEach((info)->{
			
			BulkOperate  operate=new BulkOperate();
			
			operate.setIndex(ESIndex.thingInfo);
			
			operate.setOperate(BulkOperate.OperateType.index);
			
			operate.setId(info.getKiicloudThingID());
			operate.setData(info);
			service.addData(operate);
	
			
		});
		
	}
	
	
	public void addThingStatus(ThingStatus  status,String kiiAppThingID){
		
		ESThingStatus esStatus=new ESThingStatus(status);
		
		BulkOperate oper=new BulkOperate();
		oper.setOperate(BulkOperate.OperateType.create);
		oper.setParent(kiiAppThingID);
		oper.setIndex(ESIndex.thingStatus);
		
		oper.setData(esStatus);
		
		service.addData(oper);
		
	}
	
	
	
}
