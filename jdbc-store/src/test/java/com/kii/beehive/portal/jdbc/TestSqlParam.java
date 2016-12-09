package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
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
		
		List<Integer> intList=new ArrayList<>();
		
		intList.addAll(Arrays.asList(array));
		
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
		
		List<String> strList=new ArrayList<>(Arrays.asList(strArray));
		notice.setAdditionString(strList);
		
		long id=userNoticeDao.insert(notice);
		
		
		UserNoticeDao.NoticeQuery query=new UserNoticeDao.NoticeQuery();
		
		SqlCondition cond=new SqlCondition();
		cond.setAdditionIdx(2);
		cond.setValue("iko");
		cond.setExpress(SqlCondition.SqlExpress.Like);
		
		List<SqlCondition> strCondList=new ArrayList<>();
		strCondList.add(cond);
		
		query.setStrConditionList(strCondList);
		
		SqlCondition range=new SqlCondition();
		range.setStart(0);
		range.setEnd(100);
		range.setAdditionIdx(2);
		List<SqlCondition> intCondList=new ArrayList<>();
		intCondList.add(cond);
		
		query.setIntConditionList(intCondList);
		
		List<UserNotice>  list=userNoticeDao.queryNoticeList(query,null);
		
		assertEquals(0,list.size());
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
		
		List<Integer> intList=new ArrayList<>();
		
		intList.addAll(Arrays.asList(array));
		
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
		
		List<String> strList=new ArrayList<>(Arrays.asList(strArray));
		notice.setAdditionString(strList);
		
		
		
		long id=userNoticeDao.insert(notice);
		
		UserNotice newNotice=userNoticeDao.findByID(id);
		
		assertEquals(strList.size(),newNotice.getAdditionString().size());
		
		for(int i=0;i<array.length;i++){
			assertEquals(intList.get(i),newNotice.getAdditionInteger().get(i));
		}
		
		
		for(int i=0;i<array.length;i++){
			assertEquals(strList.get(i),newNotice.getAdditionString().get(i));
		}
		
		
	}
	
}
