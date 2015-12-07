package com.kii.extension.sdk.entity.thingif;

import java.util.HashMap;
import java.util.Map;

public class Trigger {

	private TriggerTarget target;


	private Predicate  predicate;

	private Command command;

	private ServiceCode  serviceCode;



	private String title;

	private String description;

	private Map<String,Object> metadata=new HashMap<>();



	/*
	{"triggersWhat":"COMMAND",
 "predicate":{

 },
 "command":{
   "issuer":"USER:92803ea00022-a488-4e11-d7c1-018317e4",
   "actions":[{"lightness":50,"from":"trigger"}],
   "schema":"demo",
   "schemaVersion":0,
   "metadata":{"foo":"bar"},
 },
 "title":"testLight",

}
	 */
}
