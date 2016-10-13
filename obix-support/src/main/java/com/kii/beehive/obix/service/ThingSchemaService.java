package com.kii.beehive.obix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.obix.dao.ThingSchemaDao;
import com.kii.beehive.obix.store.EnumRange;
import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.ObixThingSchema;


@Component
@Transactional
public class ThingSchemaService {


	@Autowired
	private ThingSchemaDao schemaDao;


	public ObixThingSchema getThingSchema(String schemaName){
		return schemaDao.getObixThingSchemaByName(schemaName);
	}

	public ObixPointDetail getPointSchema(String schemaName, String pointName){

		return schemaDao.getObixThingSchemaByName(schemaName).getFieldCollect().get(pointName);
	}

	public EnumRange getEnumRange(String schemaName,String pointName){
		return  schemaDao.getObixThingSchemaByName(schemaName).getFieldCollect().get(pointName).getRange();
	}


}
