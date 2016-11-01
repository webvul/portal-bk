package com.kii.extension.sdk.entity.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

public class ConditionExpress extends TriggerConditionEntry{



	private List<TriggerConditionEntry> conditions=new ArrayList<>();

	public ConditionExpress(){

	}

	@JsonCreator
	public ConditionExpress(JsonNode jsonNode){

		super(jsonNode);

		if(super.getExpress().equals("and")||super.getExpress().equals("or")) {
			JsonNode subCond = jsonNode.get(super.getExpress());

			subCond.forEach(node -> {
				conditions.add(new ConditionExpress(node));
			});
		}
	}


	@JsonAnyGetter
	public Map<String, Object> getJson() {

		List<Object> list=new ArrayList<>();
		conditions.forEach(cond->{
			list.add(cond.getJson());
		});

		return Collections.singletonMap(super.getExpress(),list);
	}


	@JsonIgnore
	public void addCondition(TriggerConditionEntry condition){
		this.conditions.add(condition);
	}

	@JsonIgnore
	public void setConditions(List<TriggerConditionEntry> conditions) {
		this.conditions = conditions;
	}


}
