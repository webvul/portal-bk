package com.kii.extension.ruleengine;

import java.util.Map;

public interface EventCallback {

	void onTriggerFire(String triggerID,Map<String,String> params);

}
