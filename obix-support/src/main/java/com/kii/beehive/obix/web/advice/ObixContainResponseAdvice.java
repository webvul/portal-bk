package com.kii.beehive.obix.web.advice;


import javax.annotation.PostConstruct;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.kii.beehive.obix.common.UrlInfo;
import com.kii.beehive.obix.service.ThingSchemaService;
import com.kii.beehive.obix.store.EnumRange;
import com.kii.beehive.obix.store.LocationInfo;
import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;
import com.kii.beehive.obix.store.ObixThingSchema;
import com.kii.beehive.obix.web.entity.ObixContain;
import com.kii.beehive.obix.web.entity.ObixType;

@Controller
@ControllerAdvice("com.kii.beehive.obix.web.controller")
public class ObixContainResponseAdvice implements ResponseBodyAdvice {




	@Autowired
	private ThingSchemaService schemaService;

	private Set<String>  clsSet=new HashSet<>();

	@PostConstruct
	private void init(){

		clsSet.add(ObixThingSchema.class.getName());
		clsSet.add(ObixPointDetail.class.getName());
		clsSet.add(EnumRange.class.getName());
		clsSet.add(LocationInfo.class.getName());
		clsSet.add(PointInfo.class.getName());
		clsSet.add(ThingInfo.class.getName());
	}

	@Override
	public boolean supports(MethodParameter returnType, Class converterType) {

		String name=returnType.getGenericParameterType().getTypeName();

		return clsSet.contains(name);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {


		if(body==null){
			return null;
		}

		Class<?> cls=body.getClass();

		UrlInfo url=new UrlInfo(request.getURI());


		if(cls.equals(ObixThingSchema.class)){

			return convertSchema((ObixThingSchema)body,url);
		}else if(cls.equals(ObixPointDetail.class)){

			return convertPoint((ObixPointDetail) body,url);
		}else if(cls.equals(EnumRange.class)){

			return convertRange((EnumRange) body,url);
		}else if(cls.equals(ThingInfo.class)){
			return convertThing((ThingInfo)body,url);
		}else if(cls.equals(PointInfo.class)){
			PointInfo point=(PointInfo)body;

			return convertPoint(point.getThingSchema(),point,url);
		}else if(cls.equals(LocationInfo.class)){
			return convertLocation((LocationInfo)body,url);
		}else{
			return null;
		}
	}


	private ObixContain convertSchema(ObixThingSchema schema, UrlInfo url){


		ObixContain obix=new ObixContain();

		obix.setName(schema.getFullSchemaName());
		obix.setDisplayName(schema.getDescription());

		obix.setObixType(ObixType.OBJ);

		obix.setHref(url.getFullUrl());

		if(StringUtils.isNotBlank(schema.getSuperRef())) {
			obix.setIs(url.addToRootUrl(schema.getSuperRef()));
		}

		schema.getFieldCollect().values().forEach(p-> {
					ObixContain point=initPointContain(p, url);
					if(point.getObixType()==ObixType.ENUM){
						point.setRange(url.addToFullPath(point.getHref()+"~range"));
					}
					obix.addChild(point);
				}
		);

		return obix;

	}


	private ObixContain initPointContain(ObixPointDetail point, UrlInfo baseUrl){
		ObixContain obix=new ObixContain();

		obix.setName(point.getFieldName());
		obix.setDisplayName(point.getDescription());

		obix.setHref(point.getFieldName()+"/");

		if(StringUtils.isNotBlank(point.getSuperRef())) {
			obix.setIs(baseUrl.addToRootUrl(point.getSuperRef()));
		}
		obix.setObixType(ObixType.getInstance(point.getType()));
		obix.setWritable(point.isWritable());

		if(obix.getObixType()==ObixType.INT  || obix.getObixType()==ObixType.REAL) {
			obix.setMax(point.getMaxValue());
			obix.setMin(point.getMinValue());
		}

		obix.setUnit(point.getUnitRef());

		return obix;
	}

	private ObixContain convertPoint(ObixPointDetail point, UrlInfo url){
		ObixContain obix=initPointContain(point,url);

		if(obix.getObixType()==ObixType.ENUM){
			obix.setRange(url.addToFullPath("~range"));
		}
		obix.setHref(url.getFullUrl());

		return obix;
	}

	private ObixContain convertRange(EnumRange range,UrlInfo url){
		ObixContain obix=new ObixContain();

		range.getValueMap().forEach((k,v)->{

			ObixContain elem=new ObixContain();
			elem.setObixType(ObixType.getInstance(range.getType()));
			elem.setName(k);
			elem.setDisplayName(v.getDisplayName());

			obix.addChild(elem);

		});

		obix.setHref(url.getFullUrl());

		return obix;
	}

	private ObixContain convertThing(ThingInfo th,UrlInfo url){

		ObixThingSchema schema=schemaService.getThingSchema(th.getSchema());

		ObixContain obix= convertSchema(schema,url);

		obix.setHref(url.getFullUrl());
		obix.setDisplay(th.getName());
		obix.setName(th.getName());
		obix.setIs(url.addToRootUrl("def/"+th.getSchema()));


		th.getPointCollect().forEach((p)->{

			ObixContain obixP=convertPoint(th.getSchema(),p,url);

			obix.addChild(obixP);

		});

		return obix;

	}

	private ObixContain convertPoint(String schema,PointInfo p,UrlInfo url){

		ObixPointDetail meta=schemaService.getPointSchema(schema,p.getFieldName());

		ObixContain obix=initPointContain(meta,url);

		obix.setHref(url.getFullUrl());
		obix.setDisplay(p.getFieldName());
		obix.setName(p.getFieldName());
		obix.setIs(url.addToRootUrl("def/"+schema+"/"+p.getFieldName()));

		obix.setVal(p.getValue());

		return obix;
	}


	private ObixContain convertLocation(LocationInfo loc,UrlInfo url){

		ObixContain obix=new ObixContain();

		obix.setHref(url.getFullUrl());
		obix.setDisplay(loc.getDisplayName());
		obix.setName(loc.getLocation());
		obix.setIs(url.addToRootUrl("def/site"));

		loc.getSubLocations().forEach((k,v)->{

			ObixContain  l=new ObixContain();
			l.setObixType(ObixType.REF);
			l.setHref(v.getLocation()+"/");
			l.setIs(url.addToRootUrl("def/site"));
			l.setName(v.getLocation());
			l.setDisplay(v.getDisplayName());

			obix.addChild(l);
		});

		ObixContain  list=new ObixContain();

		list.setObixType(ObixType.LIST);
		list.setHref("equip/");
		list.setIn(url.addToRootUrl("def/commEquip"));
		list.setIs("obix:list");

		loc.getThingCollect().forEach(t->{

			ObixContain  l=new ObixContain();
			l.setObixType(ObixType.REF);

			l.setHref("equip/"+t.getName());
			l.setIs(url.addToRootUrl("def/"+t.getSchema()));
			l.setName(t.getName());

			obix.addChild(l);

		});

		return obix;
	}

}
