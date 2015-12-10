package com.kii.extension.sdk.entity.serviceextension;

public class HookConfig {

	/*
		{
	"when":"DATA_OBJECT_CREATED",
	"what":"EXECUTE_SERVER_CODE",
	"endpoint":"onScheduleAdd"
	},
	 */

	private HookWhenType when;

	private HookWhatType what;

	private String  endpoint;

	public static enum HookWhenType {
	}

	public static enum HookWhatType {
	}
}
