package com.kii.beehive.portal.jdbc.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.common.utils.StrTemplate;
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
		sqlParam.addBetween("readTime",query.readedTimeStart,query.readedTimeEnd);
		sqlParam.addBetween("createTime",query.createTimeStart,query.createTimeEnd);
		sqlParam.addLike("title",query.title);
		sqlParam.addLike("from",query.from);
		sqlParam.addEq("actionType",query.actionType);
		sqlParam.addEq("userID",query.userID);
		
		sqlParam.addPager(pager);
		
		String fullSql=sqlParam.getFullSql();
		
		List<UserNotice>  list=super.jdbcTemplate.query(fullSql,sqlParam.getParamArray(),super.getRowMapper());
		
		return list;
	}
	
	public int readed(Long id){
		
		Map<String,Object> paramMap= Collections.singletonMap("readed",true);
		
		return super.updateEntityByID(paramMap,id);
	}
	
	private static final String sqlByID= StrTemplate.gener("select * from ${0} where ${1} = ? and ${2} = ? ",TABLE_NAME,UserNotice.USER_ID,UserNotice.NOTICE_ID);
	
	public UserNotice getNoticeByID(Long noticeID, Long userID) {
	
		List<UserNotice>  list=super.jdbcTemplate.query(sqlByID,new Object[]{userID,noticeID},super.getRowMapper());
		
		if(list.isEmpty()){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	private static final String updateUnRead= StrTemplate.gener("update ${0} set ${1} = ?,${4}= ?  where ${2} = ? and ${3} = ? ",TABLE_NAME,UserNotice.READED,UserNotice.USER_ID,UserNotice.NOTICE_ID,UserNotice.READED_TIME);
	
	public int updateSign(Long noticeID, Long userID) {
		
		
		return super.jdbcTemplate.update(updateUnRead,true,userID,noticeID,new Date());
	
	}
	
	
	private static final String updateAllUnRead= StrTemplate.gener("update ${0} set ${1} = ?  where ${2} = ?  ",TABLE_NAME,UserNotice.READED,UserNotice.USER_ID);
	
	public void updateAllSign(Long userID) {
		
		super.jdbcTemplate.update(updateAllUnRead,true,userID);
		
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
		private Long userID;
		
		private String actionType;
		
		private String from;
		
		private String title;
		
		private Date createTimeStart;
		
		private Date createTimeEnd;
		
		private Date readedTimeStart;
		
		private Date readedTimeEnd;
		
		private UserNotice.MsgType type;
		
		private Boolean readed;

		public void setUserID(Long userID){
			this.userID=userID;
		}
		
		public void setActionType(String actionType) {
			this.actionType = actionType;
		}
		
		public void setFrom(String from) {
			this.from = from;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public void setCreateTimeStart(Date createTimeStart) {
			this.createTimeStart = createTimeStart;
		}
		
		public void setCreateTimeEnd(Date createTimeEnd) {
			this.createTimeEnd = createTimeEnd;
		}
		
		public void setReadedTimeStart(Date readedTimeStart) {
			this.readedTimeStart = readedTimeStart;
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
