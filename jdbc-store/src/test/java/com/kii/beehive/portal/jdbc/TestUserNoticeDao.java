package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.UserNotice;

public class TestUserNoticeDao extends TestTemplate {
	
	@Autowired
	private UserNoticeDao dao;


	@Test
	public void test(){
		
		UserNotice notice=new UserNotice();
		notice.setActionType("false2true");
		notice.setFrom("from");
		notice.setTitle("title");
		
		notice.setCreateTime(new Date());
		notice.setType(UserNotice.MsgType.ThingStatus);
		
		for(int i=0;i<10;i++) {
			notice.setUserID((long) (i%3));
			dao.insert(notice);
		}
		
		UserNoticeDao.NoticeQuery query=new UserNoticeDao.NoticeQuery();
		query.setUserID(2l);
		query.setReaded(false);
		
		List<UserNotice> list=dao.queryNoticeList(query,null);
		
		assertEquals(3,list.size());

		dao.updateSign(list.get(0).getId(),2l);
		
		list=dao.queryNoticeList(query,null);
		
		assertEquals(2,list.size());
		
		dao.updateAllSign(2l);
		
		assertEquals(0,dao.queryNoticeList(query,null).size());
		
		query.setReaded(true);
		query.setFrom("f");
		
		assertEquals(3,dao.queryNoticeList(query,null).size());
		
		
	}
}
