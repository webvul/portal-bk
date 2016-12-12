package com.kii.beehive.business.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;
import com.kii.beehive.portal.jdbc.helper.SqlCondition;
import com.kii.extension.tools.AdditionFieldType;

public class NoticeQuery {
	
	
	public void fillSqlQuery(BindClsRowMapper.SqlParam sqlParam){
		
		sqlParam.addEq("type",type);
		sqlParam.addEq("readed",readed);
		sqlParam.addBetween("readTime",readedTimeStart,readedTimeEnd);
		sqlParam.addBetween("createTime",createTimeStart,createTimeEnd);
		sqlParam.addLike("title",title);
		sqlParam.addLike("from",from);
		sqlParam.addEq("actionType",actionType);
		sqlParam.addEq("userID",userID);
		
		queryMap.values().forEach((query)->{
			
			SqlCondition cond=query.getSqlQuery();
			
			AdditionFieldType type=AdditionFieldType.getType(cond.getFieldName());
			
			if(type==AdditionFieldType.Str) {
				sqlParam.addStrCustom(cond);
			}else{
				sqlParam.addIntCustom(cond);
			}
			
		});
	}
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
	
	private Map<String,FieldQuery> queryMap=new HashMap<>();
	
	public Map<String, FieldQuery> getQueryMap() {
		return queryMap;
	}
	
	@JsonAnySetter
	public void addQueryMap(String name,FieldQuery query) {
		query.setFieldName(name);
		this.queryMap.put(name,query);
	}

	
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
