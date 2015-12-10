package com.kii.extension.sdk.entity.serviceextension;

import java.util.ArrayList;
import java.util.List;

public class HookGeneral {


	private List<TriggerConfig> triggerList=new ArrayList<>();

	public HookGeneral addTrggerConfig(TriggerConfig trigger){


		triggerList.add(trigger);

		return this;
	}

	public String getHookContext(){


	}
}
