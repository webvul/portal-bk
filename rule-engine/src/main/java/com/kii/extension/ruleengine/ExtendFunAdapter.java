package com.kii.extension.ruleengine;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ExtendFunAdapter {


	@Autowired
	private ApplicationContext applicationCtx;

	private Logger log= LoggerFactory.getLogger(ExtendFunAdapter.class);


	public Object fun(String  triggerID,String funName,Object... params) {


		log.info("extfun:"+triggerID+" funName:"+funName);

		log.info("params:"+ Arrays.toString(params));

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
