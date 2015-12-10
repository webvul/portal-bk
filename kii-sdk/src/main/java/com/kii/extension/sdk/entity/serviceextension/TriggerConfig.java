package com.kii.extension.sdk.entity.serviceextension;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class  TriggerConfig {

	private TriggerWhatType what=TriggerWhatType.EXECUTE_SERVER_CODE;

	public TriggerWhatType getWhat() {
		return what;
	}

	public void setWhat(TriggerWhatType what) {
		this.what = what;
	}

	@JsonIgnore
	public abstract String getUrl();



}
