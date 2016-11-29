package com.kii.beehive.portal.jdbc.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

@Repository
public class UserNoticeDao extends SpringSimpleBaseDao<UserNotice> {
	
	
	private static final String TABLE_NAME = "user_notice";
	
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}
	
	@Override
	protected String getKey() {
		return UserNotice.NOTICE_ID;
	}
	
	
	public List<UserNotice>  queryNoticeList(NoticeQuery query,BindClsRowMapper.Pager pager){
		
		
		BindClsRowMapper.SqlParam sqlParam=super.getSqlParam();
		
		sqlParam.addEq("type",query.type);
		sqlParam.addEq("readed",query.readed);
		sqlParam.addBetween("readTime",query.readedTimeFrom,query.readedTimeEnd);
		sqlParam.addBetween("createTime",query.createTimeFrom,query.createTimeEnd);
		sqlParam.addLike("title",query.title);
		sqlParam.addLike("from",query.from);
		sqlParam.addEq("actionType",query.actionType);
		
		
		sqlParam.addPager(pager);
		
		String fullSql=sqlParam.getFullSql();
		
		List<UserNotice>  list=super.jdbcTemplate.query(fullSql,sqlParam.getParamArray(),super.getRowMapper());
		
		return list;
	}
	
	public int readed(Long id){
		
		Map<String,Object> paramMap= Collections.singletonMap("readed",true);
		
		return super.updateEntityByID(paramMap,id);
	}
	

	public static class NoticeQuery{
		
		/*
			
	
	public  static final String NOTICE_ID ="notice_id";
	public  static final String USER_ID="user_id";
	public  static final String READED_TIME="readed_time";
	public  static final String CREATE_TIME="create_time";
	public  static final String DATA="data";
	public  static final String MESSAGE="msg_in_text";
	public  static final String TITLE="title";
	public  static final String FROM="from";
	public  static final String TYPE="notice_type";
	public  static final String READED="readed";
	public  static final String ACTION_TYPE="action_type";
	
		 */
		
		private String actionType;
		
		private String from;
		
		private String title;
		
		private Date createTimeFrom;
		
		private Date createTimeEnd;
		
		private Date readedTimeFrom;
		
		private Date readedTimeEnd;
		
		private UserNotice.MsgType type;
		
		private Boolean readed;

		
		public void setActionType(String actionType) {
			this.actionType = actionType;
		}
		
		public void setFrom(String from) {
			this.from = from;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public void setCreateTimeFrom(Date createTimeFrom) {
			this.createTimeFrom = createTimeFrom;
		}
		
		public void setCreateTimeEnd(Date createTimeEnd) {
			this.createTimeEnd = createTimeEnd;
		}
		
		public void setReadedTimeFrom(Date readedTimeFrom) {
			this.readedTimeFrom = readedTimeFrom;
		}
		
		public void setReadedTimeEnd(Date readedTimeEnd) {
			this.readedTimeEnd = readedTimeEnd;
		}
		
		public void setType(UserNotice.MsgType type) {
			this.type = type;
		}

		
		public void setReaded(Boolean readed) {
			this.readed = readed;
		}

	
	}
	
	
	
}
