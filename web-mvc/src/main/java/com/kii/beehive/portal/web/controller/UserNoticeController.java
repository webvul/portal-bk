package com.kii.beehive.portal.web.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.manager.UserNoticeManager;
import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

@RestController
@RequestMapping(value = "/users/me/notices",consumes = {MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserNoticeController {
	
	
	@Autowired
	private  UserNoticeManager manager;
	
	
	@RequestMapping(path="/unread",method = {RequestMethod.GET})
	public List<UserNotice> getAllUnReadNotice(@RequestParam(name="pager")String pagerSign){
		
		BindClsRowMapper.Pager pager= BindClsRowMapper.Pager.getInstance(pagerSign);
		
		
		return manager.getUnReadNotice(pager);
	}
	
	@RequestMapping(path="/all",method = {RequestMethod.GET})
	public List<UserNotice> getAllNotice(@RequestParam(name="pager")String sign){
		
		BindClsRowMapper.Pager pager= BindClsRowMapper.Pager.getInstance(sign);

		return manager.getAllNotice(pager);
	}
	
	
	@RequestMapping(path="/{noticeID}",method = {RequestMethod.GET})
	public UserNotice getNoticeByID(@PathVariable("noticeID") Long noticeID){
		
		return manager.getNotice(noticeID);
		
	}
	
	
	@RequestMapping(path="/{noticeID}/readed",method = {RequestMethod.PUT})
	public void setNoticeReaded(@PathVariable("noticeID") Long noticeID){
		
		manager.setReadSign(noticeID);
		
		
	}
	
	@RequestMapping(path="/all/readed",method = {RequestMethod.PUT})
	public void setAllNoticeReaded(){
		
		manager.setAllReadSign();
		
	}
	
	@RequestMapping(path="/query",method = {RequestMethod.POST})
	public List<UserNotice> queryNotice(@RequestBody UserNoticeDao.NoticeQuery query,@RequestParam(name="pager") String pagerSign){
		
		return manager.queryNotice(query, BindClsRowMapper.Pager.getInstance(pagerSign));
		
	}
	
	
}
