package com.kii.extension.ruleengine;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.entity.CurrThing;
import com.kii.extension.ruleengine.drools.entity.MultiplesValueMap;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;

@Component
public class ExtendFunAdapter {


	@Autowired
	private ApplicationContext applicationCtx;


	public Object callFun(String funName, String triggerID,Map<String,Object> status) {

		System.out.println("extfun:"+triggerID+" funName:"+funName);
		return 0;

	}

	public Object callFunction(String namespace, String funName, CurrThing th,MultiplesValueMap value){



		ExtendFunction function=applicationCtx.getBean(ExtendFunction.class);


		ExtendFunction.TriggerContext context=new ExtendFunction.TriggerContext();

		context.setCurrThingID(th.getCurrThing());
		context.setTriggerID(value.getTriggerID());
		context.setValues(value.getValues());

		return function.callFunction(namespace,funName,context);

	}

	public Object callFunction(String namespace, String funName, CurrThing th,String triggerID,ThingStatusInRule value){



		ExtendFunction function=applicationCtx.getBean(ExtendFunction.class);


		ExtendFunction.TriggerContext context=new ExtendFunction.TriggerContext();

		context.setCurrThingID(th.getCurrThing());
		context.setTriggerID(triggerID);
		context.setValues(value.getValues());

		return function.callFunction(namespace,funName,context);

	}



}
