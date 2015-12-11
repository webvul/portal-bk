package com.kii.extension.sdk.entity.serviceextension;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HookGeneral {


	Map<String,List<EventTriggerConfig>> eventMap=new HashMap<>();

	private Map<String,ScheduleTriggerConfig> triggerMap=new HashMap<>();


	public static HookGeneral getInstance(){
		return new HookGeneral();
	}

	public HookGeneral addTrggerConfig(EventTriggerConfig trigger){

		List<EventTriggerConfig> list=eventMap.getOrDefault(trigger.getUrl(),new ArrayList<EventTriggerConfig>());

		list.add(trigger);

		eventMap.put(trigger.getUrl(),list);

		return this;
	}

	public HookGeneral addTriggerConfig(ScheduleTriggerConfig trigger){
		triggerMap.put(trigger.getJobName(),trigger);

		return this;
	}


	private Object getHookContext(){


		Map<String,Object> map=new HashMap<>();

		map.putAll(eventMap);

		map.put("kiicloud://scheduler",triggerMap);

		return map;

	}

	public String generJson(ObjectMapper mapper) {
		try {
			return mapper.writeValueAsString(getHookContext());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
}
