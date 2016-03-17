package com.kii.extension.ruleengine.drools.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import com.google.common.base.Objects;

@Role(Role.Type.EVENT)
@Timestamp("createAt")
public class ThingStatusInRule {
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ThingStatusInRule that = (ThingStatusInRule) o;
		return Objects.equal(thingID, that.thingID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(thingID);
	}

	private String thingID;

	private Date createAt;

	private Map<String,Object> values=new HashMap<>();


	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public void addValue(String field,Object value){
		this.values.put(field,value);
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	@Override
	public String toString() {
		return "ThingStatus{" +
				"thingID='" + thingID + '\'' +
				", values=" + values +
				'}';
	}
}
