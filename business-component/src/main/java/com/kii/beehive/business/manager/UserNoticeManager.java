package com.kii.beehive.business.manager;


import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.business.entity.NoticeQuery;
import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

@Component
@Transactional
public class UserNoticeManager {


	@Autowired
	private UserNoticeDao noticeDao;
	
	
	public List<UserNotice> getUnReadNotice(BindClsRowMapper.Pager pager){
		
		
		NoticeQuery query=new NoticeQuery();
		query.setUserID(AuthInfoStore.getUserID());
		query.setReaded(false);
		
		BindClsRowMapper.SqlParam param=noticeDao.getSqlParam();
		query.fillSqlQuery(param);
		param.addPager(pager);
		
		return noticeDao.queryNoticeList(param);
		
	}
	
	public List<UserNotice> getAllNotice(BindClsRowMapper.Pager pager){
		NoticeQuery query=new NoticeQuery();
		query.setUserID(AuthInfoStore.getUserID());
		
		BindClsRowMapper.SqlParam param=noticeDao.getSqlParam();
		query.fillSqlQuery(param);
		param.addPager(pager);
		
		return noticeDao.queryNoticeList(param);
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
	
	public List<UserNotice> queryNotice(NoticeQuery query, BindClsRowMapper.Pager pager){
		
		query.setUserID(AuthInfoStore.getUserID());
		
		BindClsRowMapper.SqlParam param=noticeDao.getSqlParam();
		query.fillSqlQuery(param);
		param.addPager(pager);
		
		return noticeDao.queryNoticeList(param);
	}
	
}
