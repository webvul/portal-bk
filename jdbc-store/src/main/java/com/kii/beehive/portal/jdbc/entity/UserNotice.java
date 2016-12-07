package com.kii.beehive.portal.jdbc.entity;


import java.util.Date;

import org.apache.commons.lang3.StringUtils;

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
	
	public static final String ADDITIION_STRING="addition_str";
	public static final String ADDITIION_INT="addition_int";
	
	
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
	

	private String additionString;
	
	private String additionInteger;
	
	public void addAdditionString(String[] values){
		
		StringBuilder sb=new StringBuilder("^");
		
		for(int i=0;i<values.length;i++){
			sb.append("str").append(i).append(":").append(values[i]).append("^");
		}
		additionString=sb.toString();
	}
	
	private String MASK= StringUtils.repeat("0",20);
	
	public void addAdditionInteger(int[] values){
		
		StringBuilder sb=new StringBuilder();
		
		for(int i=0;i<values.length;i++){
			int val=values[i];
			String strVal=String.valueOf(val);
			String fullStrVal=StringUtils.substring(MASK+strVal,-10);
			
			sb.append(fullStrVal).append(".");
		}
		additionInteger=sb.toString();
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
