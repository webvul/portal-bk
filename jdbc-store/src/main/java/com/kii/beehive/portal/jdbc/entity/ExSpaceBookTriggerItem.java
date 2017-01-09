package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class ExSpaceBookTriggerItem extends BusinessEntity {

	public enum ExSpaceBookTriggerItemType { unlock, open_door }

	private Long exSpaceBookId;
	private String triggerId;
	private ExSpaceBookTriggerItemType type;
	private Boolean isAddedTrigger;
	private Boolean isDeletedTrigger;

	public final static String ID = "id";

	public static final String EX_SPACE_BOOK_ID = "ex_space_book_id";
	public static final String TRIGGER_ID = "trigger_id";
	public static final String TYPE = "type";
	public static final String IS_ADDED_TRIGGER = "is_added_trigger";
	public static final String IS_DELETED_TRIGGER = "is_deleted_trigger";


	@Override
	@JdbcField(column = ID)
	public Long getId() {
		return super.getId();
	}

	@JdbcField(column = EX_SPACE_BOOK_ID)
	public Long getExSpaceBookId() {
		return exSpaceBookId;
	}

	public void setExSpaceBookId(Long exSpaceBookId) {
		this.exSpaceBookId = exSpaceBookId;
	}
	@JdbcField(column = TRIGGER_ID)
	public String getTriggerId() {
		return triggerId;
	}

	public void setTriggerId(String triggerId) {
		this.triggerId = triggerId;
	}

	@JdbcField(column = TYPE , type = JdbcFieldType.EnumStr)
	public ExSpaceBookTriggerItemType getType() {
		return type;
	}

	public void setType(ExSpaceBookTriggerItemType type) {
		this.type = type;
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


}
