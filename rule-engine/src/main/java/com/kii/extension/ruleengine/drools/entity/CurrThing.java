package com.kii.extension.ruleengine.drools.entity;

import java.util.Set;

public class CurrThing {

	public  static final String NONE = "NONE";

	private Status status=Status.inInit;


	private String triggerID;
	
	public  enum Status{

		inInit,inExt,inThing,inIdle,singleTrigger;

	}

	public boolean isInit() {
		return status==Status.inInit;
	}

	public void  setStatus(CurrThing.Status  status) {

		this.status=status;

		this.currThing=NONE;
	}



	public CurrThing.Status getStatus(){
		return status;
	}

	public String getCurrThing(){
		return currThing;
	}


	private String currThing= NONE;

	private String currExt=NONE;

	public String getCurrExt() {
		return currExt;
	}

	public void setCurrExt(String currExt) {
		this.currExt = currExt;
		status=Status.inExt;
		this.currThing=NONE;

	}
	
	public void setTriggerID(String triggerID){
		this.triggerID=triggerID;
		status=Status.singleTrigger;
	}

	public void setCurrThing(String curr){

		this.currThing=curr;
		this.status=Status.inThing;
		this.currExt=NONE;
	}

	public boolean valid(Set<String> th,String triggerID){


		return status==Status.inExt
				|| (status==Status.inThing&&(th.contains(currThing)))
				|| (status==Status.singleTrigger  &&  this.triggerID.equals(triggerID));

	}

	public boolean valid(String th,String triggerID){

		return status==Status.inExt
				|| (status==Status.inThing&&(th.equals(currThing)))
				|| (status==Status.singleTrigger && this.triggerID.equals(triggerID));
	}

	@Override
	public String toString() {
		return "CurrThing{" +
				" status =" + status.name() +
				", currThing='" + currThing + '\'' +
				", currExt='"+currExt+'\''+
				", triggerID='"+triggerID+"\'"+
				'}';
	}
}
