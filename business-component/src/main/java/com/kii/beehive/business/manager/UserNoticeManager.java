package com.kii.beehive.business.manager;


import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

@Component
@Transactional
public class UserNoticeManager {


	@Autowired
	private UserNoticeDao noticeDao;
	
	
	public List<UserNotice> getUnReadNotice(BindClsRowMapper.Pager pager){
		
		
		UserNoticeDao.NoticeQuery query=new UserNoticeDao.NoticeQuery();
		query.setUserID(AuthInfoStore.getUserID());
		query.setReaded(false);
		
		
		return noticeDao.queryNoticeList(query,pager);
		
	}
	
	public List<UserNotice> getAllNotice(BindClsRowMapper.Pager pager){
		UserNoticeDao.NoticeQuery query=new UserNoticeDao.NoticeQuery();
		query.setUserID(AuthInfoStore.getUserID());
		
		return noticeDao.queryNoticeList(query,pager);
	}
	
	public UserNotice getNotice(Long noticeID){
		return noticeDao.getNoticeByID(noticeID,AuthInfoStore.getUserID());
	}
	
	public void setReadSign(Long noticeID){
		
		int num=noticeDao.updateSign(noticeID,AuthInfoStore.getUserID());
		if(num==0){
			throw new UnauthorizedException(UnauthorizedException.ACCESS_INVALID);
		}
	}
	
	public void setAllReadSign(){
		
		noticeDao.updateAllSign(AuthInfoStore.getUserID());
		
	}
	
	public void setAllReadSign(Collection<Long> ids){
		
		noticeDao.updateAllSign(AuthInfoStore.getUserID(),ids);
		
	}
	
	public List<UserNotice> queryNotice(UserNoticeDao.NoticeQuery query, BindClsRowMapper.Pager pager){
		
		query.setUserID(AuthInfoStore.getUserID());
		
		return noticeDao.queryNoticeList(query,pager);
	}
	
}
