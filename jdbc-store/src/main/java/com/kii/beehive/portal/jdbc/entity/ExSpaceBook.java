package com.kii.beehive.portal.jdbc.entity;

import java.util.Date;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class ExSpaceBook extends BusinessEntity {

	private String appCode;
	private String campusCode;
	private String bizId;
	private String bizType;
	private String userId;
	private String password;
	private String spaceCode;

	private Date beginDate;
	private Date endDate;

	private Boolean isAddedTrigger;
	private Boolean isDeletedTrigger;
	private Boolean createTriggerError;

	public final static String ID = "id";

	public static final String APP_CODE = "app_code";
	public static final String CAMPUS_CODE = "campus_code";
	public static final String BIZ_ID = "biz_id";
	public static final String BIZ_TYPE = "biz_type";
	public static final String USER_ID = "user_id";
	public static final String PASSWORD = "password";
	public static final String SPACE_CODE = "space_code";
	public static final String BEGIN_DATE = "begin_date";
	public static final String END_DATE = "end_date";
	public static final String IS_ADDED_TRIGGER = "is_added_trigger";
	public static final String IS_DELETED_TRIGGER = "is_deleted_trigger";


	@Override
	@JdbcField(column = ID)
	public Long getId() {
		return super.getId();
	}

	@JdbcField(column = APP_CODE)
	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	@JdbcField(column = CAMPUS_CODE)
	public String getCampusCode() {
		return campusCode;
	}

	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	@JdbcField(column = BIZ_ID)
	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	@JdbcField(column = BIZ_TYPE)
	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	@JdbcField(column = USER_ID)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JdbcField(column = PASSWORD)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JdbcField(column = SPACE_CODE)
	public String getSpaceCode() {
		return spaceCode;
	}

	public void setSpaceCode(String spaceCode) {
		this.spaceCode = spaceCode;
	}

	@JdbcField(column = BEGIN_DATE)
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	@JdbcField(column = END_DATE)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@JdbcField(column = IS_ADDED_TRIGGER)
	public Boolean getAddedTrigger() {
		return isAddedTrigger;
	}

	public void setAddedTrigger(Boolean addedTrigger) {
		isAddedTrigger = addedTrigger;
	}


	@JdbcField(column = IS_DELETED_TRIGGER)
	public Boolean getDeletedTrigger() {
		return isDeletedTrigger;
	}

	public void setDeletedTrigger(Boolean deletedTrigger) {
		isDeletedTrigger = deletedTrigger;
	}

	@JdbcField(column = "create_trigger_error")
	public Boolean getCreateTriggerError() {
		return createTriggerError;
	}

	public void setCreateTriggerError(Boolean createTriggerError) {
		this.createTriggerError = createTriggerError;
	}
}
