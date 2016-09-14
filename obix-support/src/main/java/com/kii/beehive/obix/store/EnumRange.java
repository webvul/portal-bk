package com.kii.beehive.obix.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumRange {

	private String name;

	private PointDataType  type;

	private Map<String,RangeElement> valueMap=new HashMap<>();


	public EnumRange(){

	}


	public EnumRange(Map<String,Object> enumMap,PointDataType type){


		enumMap.forEach((k,v)->{

			RangeElement  elem=new RangeElement();
			elem.setVal(v);
			elem.setDisplayName(k);
			elem.setName(k);

			addElement(elem);

		});

		this.type=type;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PointDataType getType() {
		return type;
	}

	public void setType(PointDataType type) {
		this.type = type;
	}

	public Map<String, RangeElement> getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map<String, RangeElement> valueMap) {
		this.valueMap = valueMap;
	}

	public void addElement(RangeElement  elem){
		valueMap.put(elem.getName(),elem);
	}

	public void addElements(List<RangeElement> elems){
		elems.forEach(this::addElement);
	}

	public boolean isExist() {
		return !valueMap.isEmpty();
	}
}
