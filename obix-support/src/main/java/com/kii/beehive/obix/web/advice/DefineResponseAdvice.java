package com.kii.beehive.obix.web.advice;


import javax.annotation.PostConstruct;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.kii.beehive.obix.common.UrlInfo;
import com.kii.beehive.obix.store.EnumRange;
import com.kii.beehive.obix.store.PointDetail;
import com.kii.beehive.obix.store.ThingSchema;
import com.kii.beehive.obix.web.entity.ObixContain;
import com.kii.beehive.obix.web.entity.ObixType;

@Controller
@ControllerAdvice("com.kii.beehive.obix.web.controller")
public class DefineResponseAdvice implements ResponseBodyAdvice {



	private Set<String>  clsSet=new HashSet<>();

	@PostConstruct
	private void init(){

		clsSet.add(ThingSchema.class.getName());
		clsSet.add(PointDetail.class.getName());
		clsSet.add(EnumRange.class.getName());
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


		if(cls.equals(ThingSchema.class)){

			return convertSchema((ThingSchema)body,url);
		}else if(cls.equals(PointDetail.class)){

			return convertPoint((PointDetail) body,url);
		}else if(cls.equals(EnumRange.class)){

			return convertRange((EnumRange) body,url);
		}else{
			return null;
		}
	}


	private ObixContain convertSchema(ThingSchema schema, UrlInfo url){


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


	private ObixContain initPointContain(PointDetail point,UrlInfo baseUrl){
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

	private ObixContain convertPoint(PointDetail point,UrlInfo url){
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

}
