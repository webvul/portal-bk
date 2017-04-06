package com.kii.beehive.obix.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.obix.store.EntityInfo;
import com.kii.beehive.obix.store.LocationView;
import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.ObixThingSchema;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;
import com.kii.beehive.obix.web.entity.ObixContain;
import com.kii.beehive.obix.web.entity.ObixType;
import com.kii.beehive.portal.store.entity.LocationInfo;

@Component
public class ObixContainConvertService {



	@Autowired
	private ObixUnitIndexService unitService;


	public ObixContain getFullObix(ThingInfo thing,String baseUrl){


		ObixContain obix= getEmbeddedObix(thing,baseUrl);

		thing.getPointCollect().forEach((p)->{


			ObixContain obixP= getEmbeddedObix(p,baseUrl);

			obix.addChild(obixP);

		});
		
		ObixContain obixL = getEmbeddedObix(thing.getLocation(), baseUrl + "/site");
		obix.addChild(obixL);

		return obix;
	}


	public ObixContain getFullObix(PointInfo point, ObixThingSchema  thSchema, String baseUrl){



		ObixContain obixP=getEmbeddedObix(point,baseUrl);

		obixP.setObixType(ObixType.OBJ);
		
		obixP.setHref(baseUrl + "/things/" + point.getThingID() + "/" + point.getFieldName());

		obixP.setDisplay(point.getFieldName());
		obixP.setName(point.getFieldName());
		obixP.setVal(point.getValue());
		obixP.addToIs(baseUrl+"/def/contract/siteRef" );
		obixP.addToIs(baseUrl+"/def/schema/"+point.getSchema().getThingSchema()+"/"+point.getFieldName());
		obixP.addToIs(baseUrl+"/def/contract/commPoint");


		if(point.getSchema().isWritable()) {
			obixP.addToIs("obix:writablePoint");
		}else{
			obixP.addToIs("obix:point");
		}

		ObixContain  loc=getEmbeddedObix(point.getLocation(),baseUrl+"/site/");

		loc.setName("siteRef");

		obixP.addChild(loc);

		ObixContain obixT= convertThingSchema(thSchema,baseUrl);

		obixT.setName("equipRef");
		obixT.setObixType(ObixType.REF);
		obixP.addChild(obixT);

		return obixP;

	}


	public ObixContain getFullObix(LocationView view, String baseUrl){
		ObixContain obix=new ObixContain();

		obix.setObixType(ObixType.OBJ);

		obix.addToIs(baseUrl+"/def/contract/location");
		LocationInfo loc=view.getLocation();


		obix.addChild(getLocList(view, baseUrl));


		if(loc.getParent()!=null) {
			ObixContain parent = getEmbeddedObix(loc.getParent(), baseUrl);
			parent.setName("parentLoc");
			obix.addChild(parent);
		}

		ObixContain  eList=new ObixContain();
		eList.setName("entityList");
		eList.setObixType(ObixType.LIST);
		eList.setOf(baseUrl+"/def/contract/commEntity");

		view.getEntityCollect().forEach(e->{
			eList.addChild(getEmbeddedObix(e,baseUrl));
		});
		obix.addChild(eList);

		return obix;
	}

	public ObixContain getLocList(LocationView view, String baseUrl) {
		ObixContain list=new ObixContain();
		list.setObixType(ObixType.LIST);
		list.setOf(baseUrl+"/def/contract/location");


		view.getLocation().getSubLocations().keySet().forEach(l-> {
					list.addChild(getEmbeddedObix(l, baseUrl));
					return;
				}
		);
		return list;
	}


	public  ObixContain getEmbeddedObix(String  loc,String baseUrl){

		ObixContain obix=new ObixContain();

		obix.setObixType(ObixType.REF);

		StringBuilder  fullLoc=new StringBuilder();
		if(loc==null){
			loc="";
		}
		if(loc.length()>=2) {
			fullLoc.append(loc.substring(0, 2)).append("/");
		}
		if(loc.length()>=4){
			fullLoc.append(loc.substring(2, 4)).append("/");
		}
		if(loc.length()>=5){
			fullLoc.append(loc.substring(4, 5)).append("/");
		}
		if(loc.length()>=7){
			fullLoc.append(loc.substring(6, 7)).append("/");
		}
		if(loc.length()==9){
			fullLoc.append(loc.substring(7, 9)).append("/");
		}


		obix.setHref(baseUrl+"/"+fullLoc.toString());
		obix.setDisplay(loc);
		obix.setName("siteRef");
		obix.addToIs(baseUrl+"/def/contract/location");
		return obix;
	}


	public ObixContain getEmbeddedObix(ThingInfo thing,String baseUrl){


		ObixContain obix= convertThingSchema(thing.getSchema(),baseUrl);
		
		obix.setHref(baseUrl + "/things/" + thing.getName());
		obix.setDisplay(thing.getName());
		obix.setName(thing.getName());

		obix.addToIs("h:equip");
		obix.addToIs(baseUrl+"/def/contract/commEquip");
		obix.addToIs(baseUrl+"/def/contract/siteRef");


		obix.addToIs("h:ahu");

		return obix;

	}

	private ObixContain convertThingSchema(ObixThingSchema schema, String  baseUrl){


		ObixContain obix=new ObixContain();

		obix.setName(schema.getName());
		obix.setDisplayName(schema.getDescription());

		obix.setObixType(ObixType.OBJ);

		String defUrl=baseUrl+"/def/schema/";
		obix.setHref(defUrl+obix.getName());

		if(StringUtils.isNotBlank(schema.getSuperRef())) {
			obix.setIs(baseUrl+schema.getSuperRef());
		}

		return obix;
	}

	public ObixContain getEmbeddedObix(EntityInfo p, String baseUrl){

		if(p instanceof  PointInfo){
			return getEmbeddedObix((PointInfo)p,baseUrl);
		}else{
			return getEmbeddedObix((ThingInfo)p,baseUrl);
		}

	}


	public ObixContain getEmbeddedObix(PointInfo p, String baseUrl){


		ObixPointDetail point=p.getSchema();

		ObixContain obix=new ObixContain();


		obix.setName(point.getFieldName());
		obix.setDisplayName(point.getDescription());

		obix.setHref(point.getFieldName()+"/");

		if(StringUtils.isNotBlank(point.getSuperRef())) {
			obix.setIs(baseUrl+"/def/contract"+point.getSuperRef());
		}
		obix.setObixType(ObixType.getInstance(point.getType()));
		obix.setWritable(point.isWritable());

		if(obix.getObixType()==ObixType.INT  || obix.getObixType()==ObixType.REAL) {
			obix.setMax(point.getMaxValue());
			obix.setMin(point.getMinValue());
		}

		obix.setUnit(unitService.getObixUnitRef(point.getUnitRef()));

		String  thingSchemaName=p.getSchema().getThingSchema();

		if(obix.getObixType()==ObixType.ENUM){
			obix.setRange(baseUrl+"/def/schema/"+thingSchemaName+"/"+p.getFieldName()+"/~range");
		}

		obix.setVal(p.getValue());
		obix.addToIs(baseUrl+"/def/schema/"+thingSchemaName+"/"+p.getFieldName());
		obix.addToIs(baseUrl+"/def/contract/commPoint");

		if(p.getSchema().isWritable()) {
			obix.addToIs("obix:writablePoint");
		}else{
			obix.addToIs("obix:point");
		}

		obix.setObixType(ObixType.REF);

		return obix;
	}

}
