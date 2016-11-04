package com.kii.beehive.obix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.service.IndustryTemplateService;
import com.kii.beehive.industrytemplate.ThingSchema;
import com.kii.beehive.obix.store.EnumRange;
import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.ObixThingSchema;


@Component
@Transactional
public class ThingSchemaService {


	@Autowired
	private IndustryTemplateService   schemaDao;




	public ObixThingSchema getThingSchema(String schemaName){

		ThingSchema  schema= schemaDao.getThingSchemaByName(schemaName);

		return  new ObixThingSchema(schema);
	}

	public ObixPointDetail getPointSchema(String schemaName, String pointName){

		return getThingSchema(schemaName).getFieldCollect().get(pointName);
	}

	public EnumRange getEnumRange(String schemaName,String pointName){
		return  getThingSchema(schemaName).getFieldCollect().get(pointName).getRange();
	}


}
