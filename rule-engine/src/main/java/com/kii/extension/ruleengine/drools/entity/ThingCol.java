package com.kii.extension.ruleengine.drools.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class ThingCol {

	protected String name;

	protected Set<String> things=new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getThings() {
		return things;
	}

	public void setThings(Set<String> things) {
		this.things=things;
	}


	public void addThing(String thing){
		things.add(thing);
	}

	public void setThingCol(Collection<String> things) {
		this.things.addAll(things);
	}

}
