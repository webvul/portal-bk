package com.kii.beehive.portal.jdbc.entity;


import java.util.Date;
import java.util.Map;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class UserNotice extends DBEntity {
	
	
	public  static final String NOTICE_ID ="notice_id";
	public  static final String USER_ID="user_id";
	public  static final String READED_TIME="readed_time";
	public  static final String DATA="data";
	public  static final String MESSAGE="msg_in_text";
	public  static final String TITLE="title";
	public  static final String FROM="from";
	public  static final String TYPE="notice_type";
	public  static final String READED="readed";
	
	
	private boolean readed=false;

	private Date readTime;

	private Map<String,Object> data;
	
	private String msgInText;

	private String  title;

	private String from;

	private MsgType type;
	
	private Long userID;
	
	@JdbcField(column = USER_ID)
	public Long getUserID() {
		return userID;
	}
	
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	
	@Override
	@JdbcField(column = NOTICE_ID)
	public Long getId(){
		return super.getId();
	}
	
	
	@JdbcField(column = READED)
	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

	@JdbcField(column = READED_TIME)
	public Date getReadTime() {
		return readTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	@JdbcField(column=MESSAGE)
	public String getMsgInText() {
		return msgInText;
	}

	public void setMsgInText(String msgInText) {
		this.msgInText = msgInText;
	}

	@JdbcField(column = TITLE)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JdbcField(column = FROM)
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@JdbcField(column = TYPE)
	public MsgType getType() {
		return type;
	}

	public void setType(MsgType type) {
		this.type = type;
	}
	
	@JdbcField(column = DATA,type = JdbcFieldType.Json)
	public Map<String, Object> getData() {
		return data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public enum MsgType{
		ThingStatus;
	}

}
