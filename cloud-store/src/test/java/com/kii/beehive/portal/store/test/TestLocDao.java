package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.LocationDao;
import com.kii.beehive.portal.service.SubLocInfo;
import com.kii.beehive.portal.store.entity.LocationInfo;
import com.kii.beehive.portal.store.entity.LocationType;

public class TestLocDao extends TestTemplate {

	@Autowired
	private LocationDao  locDao;

	@Autowired
	private ObjectMapper mapper;



	String json1="{\n" +
			"\"prefix\":\"test\",\n" +
			"\"from\":0,\n" +
			"\"to\":10,\n" +
			"\"length\":4\n"+
			"}\n";

	String json2="\n" +
			"{\n" +
			"\"prefix\":\"testCh\",\n" +
			"\"from\":\"a\",\n" +
			"\"to\":\"z\"\n" +
			"}";

	String json3="{\n" +
			"\"array\":[\"a1\",\"a2\",\"b1\",\"b2\",\"c1\",\"c2\"]\n" +
			"}";

	String json4="{\n" +
			"\"from\":0,\n" +
			"\"to\":10\n" +
			"}\n";


	@Test
	public void testLocationType(){

		String loc="0103w-M01";

		assertEquals(LocationType.area,LocationType.getTypeByLocation(loc));

		assertEquals("01",LocationType.building.getLevelSeq(loc));
		assertEquals("03",LocationType.floor.getLevelSeq(loc));
		assertEquals("w",LocationType.partition.getLevelSeq(loc));
		assertEquals("M01",LocationType.area.getLevelSeq(loc));


		String subLoc="0204a";

		assertEquals(LocationType.partition,LocationType.getTypeByLocation(subLoc));

		subLoc="0204";

		assertEquals(LocationType.floor,LocationType.getTypeByLocation(subLoc));

		subLoc="02";

		assertEquals(LocationType.building,LocationType.getTypeByLocation(subLoc));


		assertEquals(LocationType.area,LocationType.getNextLevel(LocationType.partition));

		assertEquals(LocationType.floor,LocationType.getNextLevel(LocationType.building));

	}

	@Test
	public void fillRootLoc() {


		SubLocInfo locInfo=new SubLocInfo();
		locInfo.setFrom(1);
		locInfo.setTo(3);

		locDao.generTopLocation(locInfo);

	}

	@Test
	public void fillSubLoc(){

		SubLocInfo locInfo=new SubLocInfo();
		locInfo.setFrom(1);
		locInfo.setTo(4);

		locDao.generSubLevelInUpper("01",locInfo);

		List<String> pList= Arrays.asList("w","e","s","n");
		locInfo.setArray(pList);

		for(int i=1;i<=4;i++){

			locDao.generSubLevelInUpper("010"+i,locInfo);

			SubLocInfo areaInfo=new SubLocInfo();
			areaInfo.setPrefix(LocationInfo.AreaType.values()[(i*i)%4].name());
			areaInfo.setFrom(0);
			areaInfo.setTo(11);

			for(String p:pList){

				locDao.generSubLevelInUpper("010"+i+p,areaInfo);

			}

		}



	}


	@Test
	public void initNumSeq() throws IOException {


		SubLocInfo subloc=mapper.readValue(json1, SubLocInfo.class);

		List<String> list=locDao.getSeq(subloc);

		assertEquals(list.size(),11);
		assertEquals(list.get(0),"test0000");

		assertEquals(list.get(10),"test0010");

	}

	@Test
	public void initCharSeq() throws IOException {


		SubLocInfo subloc=mapper.readValue(json2, SubLocInfo.class);

		List<String> list=locDao.getSeq(subloc);

		assertEquals(list.size(),26);
		assertEquals(list.get(0),"testCha");

		assertEquals(list.get(7),"testChh");

	}


	@Test
	public void initArraySeq() throws IOException {


		SubLocInfo subloc=mapper.readValue(json3, SubLocInfo.class);

		List<String> list=locDao.getSeq(subloc);

		assertEquals(list.size(),6);
		assertEquals(list.get(0),"a1");

		assertEquals(list.get(4),"c1");

	}

	@Test
	public void initSimpleNumSeq() throws IOException {


		SubLocInfo subloc=mapper.readValue(json4, SubLocInfo.class);

		List<String> list=locDao.getSeq(subloc);

		assertEquals(list.size(),11);
		assertEquals(list.get(0),"0");

		assertEquals(list.get(10),"10");

	}


}
