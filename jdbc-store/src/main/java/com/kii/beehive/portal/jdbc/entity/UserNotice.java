package com.kii.beehive.portal.jdbc.entity;


import static com.kii.beehive.portal.jdbc.helper.BindClsRowMapper.ADDITIION_INT;
import static com.kii.beehive.portal.jdbc.helper.BindClsRowMapper.ADDITIION_STRING;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRawValue;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class UserNotice extends DBEntity {
	
	
	public  static final String NOTICE_ID ="user_notice_id";
	public  static final String USER_ID="beehive_user_id";
	public  static final String READED_TIME="readed_time";
	public  static final String CREATE_TIME="create_time";
	public  static final String DATA="data";
	public  static final String MESSAGE="msg_in_text";
	public  static final String TITLE="title";
	public  static final String FROM="from_where";
	public  static final String TYPE="notice_type";
	public  static final String READED="readed";
	public  static final String ACTION_TYPE="action_type";
	
	
	
	private Boolean readed=false;

	private Date readTime;
	
	private Date createTime;

	private String  data;
	
	private String msgInText;

	private String  title;

	private String from;

	private MsgType type;
	
	private Long userID;
	
	private String actionType;
	

	private List<String> additionString=new ArrayList<>();
	
	private List<Integer> additionInteger=new ArrayList<>();
	
	@JdbcField(column=ADDITIION_STRING,type=JdbcFieldType.AdditionStr)
	public List<String>  getAdditionString() {
		return additionString;
	}
	
	public void setAdditionString(List<String>  additionString) {
		this.additionString = additionString;
	}
	
	@JdbcField(column = ADDITIION_INT,type=JdbcFieldType.AdditionInt)
	public List<Integer> getAdditionInteger() {
		return additionInteger;
	}
	
	public void setAdditionInteger(List<Integer> additionInteger) {
		this.additionInteger = additionInteger;
	}
	


	
	
	
	
	@JdbcField(column = CREATE_TIME)
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@JdbcField(column = ACTION_TYPE)
	public String getActionType() {
		return actionType;
	}
	
	public void setActionType(String actionType) {
		
		this.actionType = actionType;
	}
	
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
	public Boolean getReaded() {
		return readed;
	}

	public void setReaded(Boolean readed) {
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
	
	
	@JdbcField(column=TYPE,type = JdbcFieldType.EnumStr)
	public MsgType getType() {
		return type;
	}

	public void setType(MsgType type) {
		this.type = type;
	}
	
	@JsonRawValue
	@JdbcField(column = DATA)
	public String getData() {
		return data;
	}
	
	public void setData(String  data) {
		this.data = data;
	}
	
	public enum MsgType{
		ThingStatus;
	}
	


}
