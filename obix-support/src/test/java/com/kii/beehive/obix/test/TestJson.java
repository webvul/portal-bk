package com.kii.beehive.obix.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.kii.beehive.obix.web.entity.ObixContain;
import com.kii.beehive.obix.web.entity.ObixType;

public class TestJson {

	private Logger log= LoggerFactory.getLogger(TestJson.class);


	private ObjectMapper mapper=new ObjectMapper();

	public TestJson(){
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

	}



	@Test
	public void testObixContain() throws JsonProcessingException {


		ObixContain schema=new ObixContain();
		schema.setUnit("m");
		schema.setHref("http://");

		ObixContain point=new ObixContain();
		point.setHref("htt");
		point.setObixType(ObixType.BOOL);

		schema.addChild(point);


		log.info(mapper.writeValueAsString(schema));

	}
}
