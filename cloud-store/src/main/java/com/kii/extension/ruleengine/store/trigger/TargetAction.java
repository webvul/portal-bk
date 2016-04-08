package com.kii.extension.ruleengine.store.trigger;

import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class TargetAction {

	private ThingCommand command;

	private ServiceCode serviceCode;

	public ServiceCode getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(ServiceCode serviceCode) {
		this.serviceCode = serviceCode;
	}

	public ThingCommand getCommand() {
		return command;
	}

	public void setCommand(ThingCommand command) {
		this.command = command;
	}


}
