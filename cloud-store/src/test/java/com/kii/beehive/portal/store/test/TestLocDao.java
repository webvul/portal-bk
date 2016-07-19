package com.kii.beehive.portal.store.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.LocationDao;

public class TestLocDao extends TestTemplate {

	@Autowired
	private LocationDao  locDao;

	@Autowired
	private ObjectMapper mapper;



	String json1="{\n" +
			"\"prefix\":\"test1\",\n" +
			"\"from\":0,\n" +
			"\"to\":10\n" +
			"}\n";

	String json2="\n" +
			"{\n" +
			"\"prefix\":\"test2\",\n" +
			"\"from\":\"a\",\n" +
			"\"to\":\"z\"\n" +
			"}";

	String json3="{\n" +
			"\"prefix\":\"test3\",\n" +
			"\"array\":[\"a1\",\"a2\",\"b1\",\"b2\",\"c1\",\"c2\"]\n" +
			"}";


	@Test
	public void initRoot(){


		}";
		locInfo.set
	}


}
