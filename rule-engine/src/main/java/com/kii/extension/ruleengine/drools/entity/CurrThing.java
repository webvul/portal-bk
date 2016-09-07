package com.kii.extension.ruleengine.drools.entity;

import java.util.Set;

public class CurrThing {

	private Status status=Status.inInit;

	public  enum Status{

		inInit,inExt,inThing,inIdle;

	}

	public boolean isInit() {
		return status==Status.inInit;
	}

	public void  setStatus(CurrThing.Status  status) {

		this.status=status;
	}


	public CurrThing.Status getStatus(){
		return status;
	}

	public String getCurrThing(){
		return currThing;
	}

	private String currThing="NONE";


	public void setCurrThing(String curr){
		this.currThing=curr;
		status=Status.inThing;
	}

	public boolean valid(Set<String> th){


		return status==Status.inExt|| (status==Status.inThing&&(th.contains(currThing)));

	}

	public boolean valid(String th){

		return status==Status.inExt|| (status==Status.inThing&&(th.equals(currThing)));
	}

	@Override
	public String toString() {
		return "CurrThing{" +
				" status =" + status.name() +
				", currThing='" + currThing + '\'' +
				'}';
	}
}
