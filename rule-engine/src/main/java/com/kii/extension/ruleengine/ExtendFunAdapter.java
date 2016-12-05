package com.kii.extension.ruleengine;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ExtendFunAdapter {


	@Autowired
	private ApplicationContext applicationCtx;



	public Object fun(String  triggerID,String funName,Object... params) {


		System.out.println("extfun:"+triggerID+" funName:"+funName);

		System.out.println("params:"+ Arrays.toString(params));

		return 10;

	}

//	public Object callFunction(String namespace, String funName, CurrThing th,MultiplesValueMap value){
//
//
//
//		ExtendFunction function=applicationCtx.getBean(ExtendFunction.class);
//
//
//		ExtendFunction.TriggerContext context=new ExtendFunction.TriggerContext();
//
//		context.setCurrThingID(th.getCurrThing());
//		context.setTriggerID(value.getTriggerID());
//		context.setValues(value.getValues());
//
//		return function.callFunction(namespace,funName,context);
//
//	}
//
//	public Object callFunction(String namespace, String funName, CurrThing th,String triggerID,ThingStatusInRule value){
//
//
//
//		ExtendFunction function=applicationCtx.getBean(ExtendFunction.class);
//
//
//		ExtendFunction.TriggerContext context=new ExtendFunction.TriggerContext();
//
//		context.setCurrThingID(th.getCurrThing());
//		context.setTriggerID(triggerID);
//		context.setValues(value.getValues());
//
//		return function.callFunction(namespace,funName,context);
//
//	}
//


}
