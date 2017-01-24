package com.kii.extension.ruleengine.drools.entity;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class AtomicCurrThing {
	
	private AtomicReference<CurrThing>  contain=new AtomicReference<>(new CurrThing());
	
	
	public void setThing(CurrThing thing){
		contain.set(thing);
	}
	
	public CurrThing getThing(){
		return contain.get();
	}
	
	public CurrThing updateAndGet(UnaryOperator<CurrThing> fun){
		return contain.updateAndGet(fun);
	};

	public void compareAndSet(CurrThing th, CurrThing.Status newStatus){
		
		CurrThing newTh=new CurrThing(th);
		newTh.setStatus(newStatus);
		
		contain.compareAndSet(th,newTh);
	}
	
	public boolean isInit(){
		return getThing().isInit();
	}
	
	public boolean valid(Set<String> th, String triggerID){
		
		return contain.get().valid(th,triggerID);
	}
	
	public boolean valid(String th,String triggerID) {
		
		return contain.get().valid(th, triggerID);
	}
				
}
