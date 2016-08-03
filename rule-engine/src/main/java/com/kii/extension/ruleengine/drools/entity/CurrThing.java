package com.kii.extension.ruleengine.drools.entity;

import java.util.Set;

public class CurrThing {

	private boolean isInit=false;

	private boolean inExt=false;

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean init) {
		isInit = init;
	}


	private String currThing="NONE";

	public String getCurrThing(){
		return currThing;
	}


	public void withExtValue(){

		inExt=true;
	}

	public void setCurrThing(String curr){
		this.currThing=curr;
		inExt=false;
	}

	public boolean valid(Set<String> th){


		return inExt|| (!isInit&&(th.contains(currThing)));

	}

	public boolean valid(String th){

		return inExt|| (!isInit&&(th.equals(currThing)));
	}

	@Override
	public String toString() {
		return "CurrThing{" +
				"isInit=" + isInit +
				", inExt=" + inExt +
				", currThing='" + currThing + '\'' +
				'}';
	}
}
