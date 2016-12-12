package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;
import com.kii.beehive.portal.jdbc.helper.SqlCondition;

public class TestSqlParam extends TestTemplate{
	
	@Autowired
	private UserNoticeDao userNoticeDao;
	
	
	@Test
	public void testQuery(){
		
		UserNotice notice=new UserNotice();
		notice.setCreateTime(new Date());
		notice.setFrom("foo");
		notice.setUserID(0l);
		notice.setTitle("title");
		notice.setType(UserNotice.MsgType.ThingStatus);
		
		Integer[] array=new Integer[]{100,1000,50,11111111,Integer.MAX_VALUE,Integer.MIN_VALUE};
		
		Map<Integer, Integer> intList = getIntegerMap(array);
//		intList.addAll(Arrays.asList(array));
		
		notice.setAdditionInteger(intList);
		
		String[] strArray=new String[]{
				"就一层不需要上下水",
				"Mikov","  10:40:35",
				"喝水靠桶，排水靠沟",
				"地下室-CRH380  10:41:06",
				"难道是村里的旱厕。。。",
				"Mikov  10:41:25",
				"排队等狒狒总招工",
				"terry  10:41:42",
				"澳洲产税几何？"};
		
		
		Map<Integer, String> strList = getStringMap(strArray);
		notice.setAdditionString(strList);
		
		long id=userNoticeDao.insert(notice);
		
		
		BindClsRowMapper.SqlParam query=userNoticeDao.getSqlParam();
		
		SqlCondition cond=new SqlCondition();
		cond.setFieldName("Str2");
		cond.setValue("iko");
		cond.setExpress(SqlCondition.SqlExpress.Like);
		
		SqlCondition range=new SqlCondition();
		range.setStart(0);
		range.setEnd(100);
		range.setFieldName("Int2");
		
		query.addIntCustom(range);
		query.addStrCustom(cond);
		
		List<UserNotice>  list=userNoticeDao.queryNoticeList(query);
		
		assertEquals(0,list.size());
	}
	
	private Map<Integer, String> getStringMap(String[] strArray) {
		Map<Integer,String> strList=new HashMap<>();
		
		for(int i=0;i<strArray.length;i++){
			strList.put(i,strArray[i]);
		}
		return strList;
	}
	
	private Map<Integer, Integer> getIntegerMap(Integer[] array) {
		Map<Integer,Integer> intList=new HashMap<>();
		
		for(int i=0;i<array.length;i++){
			intList.put(i,array[i]);
		}
		return intList;
	}
	
	@Test
	public void testParam(){
		
		UserNotice notice=new UserNotice();
		notice.setCreateTime(new Date());
		notice.setFrom("foo");
		notice.setUserID(0l);
		notice.setTitle("title");
		notice.setType(UserNotice.MsgType.ThingStatus);
		
		Integer[] array=new Integer[]{100,1000,10001,11111111,Integer.MAX_VALUE,Integer.MIN_VALUE};
		
		notice.setAdditionInteger(getIntegerMap(array));
		
		String[] strArray=new String[]{
				"就一层不需要上下水",
				"Mikov","  10:40:35",
		"喝水靠桶，排水靠沟",
		"地下室-CRH380  10:41:06",
		"难道是村里的旱厕。。。",
		"Mikov  10:41:25",
		"排队等狒狒总招工",
		"terry  10:41:42",
		"澳洲产税几何？"};
		
		notice.setAdditionString(getStringMap(strArray));
		
		
		
		long id=userNoticeDao.insert(notice);
		
		UserNotice newNotice=userNoticeDao.findByID(id);
		
		assertEquals(strArray.length,newNotice.getAdditionString().size());
		
		for(int i=0;i<array.length;i++){
			assertEquals(array[i],newNotice.getAdditionInteger().get(i));
		}
		
		
		for(int i=0;i<array.length;i++){
			assertEquals(strArray[i],newNotice.getAdditionString().get(i));
		}
		
		
	}
	
}
