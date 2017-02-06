package com.kii.extension.ruleengine.drools.entity;

import java.util.HashSet;
import java.util.Set;

public class CurrThing {


	private Status status=Status.inInit;
	
	private String triggerID;
	
	private Set<String> currThings = new HashSet<>();
	
	public  enum Status{

		inInit,inThing,inIdle,singleTrigger;

	}
	
	public CurrThing(){
		
	}
	
	public CurrThing(CurrThing th){
		
		this.status=th.status;
		this.triggerID=th.triggerID;
		this.currThings=new HashSet<>(th.currThings);
	}

	public boolean isInit() {
		return status==Status.inInit;
	}

	public void  setStatus(CurrThing.Status  status) {

		this.status=status;
	}

	public void cleanThings(){
		this.currThings.clear();
	}


	public CurrThing.Status getStatus(){
		return status;
	}

	public Set<String> getCurrThings(){
		return currThings;
	}

	public void setCurrThings(Set<String> ids){
		currThings=new HashSet<>(ids);
	}

	
	public void setTriggerID(String triggerID){
		this.triggerID=triggerID;
		status=Status.singleTrigger;
	}
	
	private boolean exists(Set<?> set2){

		Set<?>  a=new HashSet<>(currThings);
		Set<?>  b=new HashSet<>(set2);
		
		return a.removeAll(b);
	}

	public boolean valid(Set<String> th,String triggerID){
		
		return  (status==Status.inThing&&(exists(th)))
				|| (status==Status.singleTrigger  &&  this.triggerID.equals(triggerID));

	}

	public boolean valid(String th,String triggerID){

		return
				(status==Status.inThing&&(currThings.contains(th)))
				|| (status==Status.singleTrigger && this.triggerID.equals(triggerID));
	}

	@Override
	public String toString() {
		return "CurrThing{" +
				" status =" + status.name() +
				", currThings='" + currThings + '\'' +
				", triggerID='"+triggerID+"\'"+
				'}';
	}
}
