package com.kii.extension.sdk.entity.serviceextension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HookGeneral {


	private Map<String,List<EventTriggerConfig>> eventMap=new HashMap<>();

	private Map<String,ScheduleTriggerConfig> triggerMap=new HashMap<>();


	public static HookGeneral getInstance(){
		return new HookGeneral();
	}

	public HookGeneral addEventTriggerConfigs(List<EventTriggerConfig> triggerList){
		triggerList.forEach(trigger->addTriggerConfig(trigger));

		return this;
	}

	public HookGeneral addScheduleTriggerConfigs(List<ScheduleTriggerConfig> triggerList){
		triggerList.forEach(trigger->addTriggerConfig(trigger));

		return this;
	}

	public  HookGeneral addTriggerConfig(EventTriggerConfig trigger){

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

		eventMap.replaceAll((k,v)->{

			List<EventTriggerConfig>  list=(List<EventTriggerConfig>)v;

			for (EventTriggerConfig c:list) {
				c.setUrl(null);
			}
			return list;
		});

		if(!eventMap.isEmpty()) {
			map.putAll(eventMap);
		}

		if(!triggerMap.isEmpty()) {
			map.put("kiicloud://scheduler", triggerMap);
		}


		return map;

	}

	public String generJson(ObjectMapper mapper) {

		if(triggerMap.isEmpty()&&eventMap.isEmpty()){
			return null;
		}

		try {
			return mapper.writeValueAsString(getHookContext());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
}
