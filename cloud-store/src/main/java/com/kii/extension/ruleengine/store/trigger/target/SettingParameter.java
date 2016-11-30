package com.kii.extension.ruleengine.store.trigger.target;

import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;

public class SettingParameter extends ExecuteTarget {




	@Override
	public String getType() {
		return "SettingParameter";
	}


	private String groupName;


	private String parameterName;

	private String valueExpress;


	private boolean extensiionParam;


	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getValueExpress() {
		return valueExpress;
	}

	public void setValueExpress(String valueExpress) {
		this.valueExpress = valueExpress;
	}

	public boolean isExtensiionParam() {
		return extensiionParam;
	}

	public void setExtensiionParam(boolean extensiionParam) {
		this.extensiionParam = extensiionParam;
	}
}
