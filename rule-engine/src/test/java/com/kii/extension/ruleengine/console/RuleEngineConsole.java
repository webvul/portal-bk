package com.kii.extension.ruleengine.console;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;


public class RuleEngineConsole {

	public static void main(String[] argc){

		ClassPathXmlApplicationContext  context=new ClassPathXmlApplicationContext("classpath:ruleTestContext.xml");

		EngineService engine=context.getBean(EngineService.class);

		ObjectMapper mapper=context.getBean(ObjectMapper.class);

		System.out.println(">>> input command\n");

		String triggerID=null;

		Set<String> triggerSet=new HashSet<>();
		Map<String,Set<String>>  thingMap=new HashMap<>();

		engine.updateExternalValue("demo","one",111);

		engine.updateExternalValue("demo","two",222);


		engine.updateThingStatus("a",getStatus("foo=100,bar=-10"),new Date());
		engine.updateThingStatus("b",getStatus("foo=10,bar=-100"),new Date());
		engine.updateThingStatus("c",getStatus("foo=-10,bar=10"),new Date());
		engine.updateThingStatus("d",getStatus("foo=-100,bar=100"),new Date());



		Set<String> ths=new HashSet<>();
		ths.add("a");
		ths.add("b");
		ths.add("c");
		ths.add("d");

		thingMap.put("comm",ths);
		thingMap.put("one",ths);
		thingMap.put("two",ths);
		thingMap.put("three", Collections.singleton("c"));
		thingMap.put("four", Collections.singleton("d"));
		thingMap.put("five",ths);
		thingMap.put("six",ths);
		thingMap.put("seven", Collections.singleton("b"));
		thingMap.put("eight", Collections.singleton("a"));


		while(true){


			try {
//				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

				String input = readLine();

				if(StringUtils.isEmpty(input)){
					continue;
				}

				String[] arrays= StringUtils.tokenizeToStringArray(input," ");

				String cmd=arrays[0];

				if(cmd.equals("exit")){
					System.exit(0);
				}

				if(cmd.equals("setStatus")){
					String params=arrays[2];

					ThingStatus status=getStatus(params);

					engine.updateThingStatus(arrays[1],status,new Date());

				}

				if(cmd.equals("enable")){

					engine.enableTrigger(triggerID);
				}
				if(cmd.equals("disable")){
					engine.disableTrigger(triggerID);
				}
				if(cmd.equals("remove")){
					engine.removeTrigger(triggerID);
				}
				if(cmd.equals("setThingCol")){

					String summaryID=arrays[1];
					String[] things=arrays[2].split(",");

					Set<String> set=new HashSet<>();
					set.addAll(Arrays.asList(things));

					thingMap.put(summaryID,set);
				}
				if (cmd.equals("newMul")){

					String id=String.valueOf(System.currentTimeMillis());

					String json=getFileContext(arrays[1]);

					MultipleSrcTriggerRecord record=mapper.readValue(json,MultipleSrcTriggerRecord.class);
					record.setId(id);


					engine.createMultipleSourceTrigger(record,thingMap);
					triggerID=id;

					System.out.println("create trigger "+triggerID);

					triggerSet.add(id);

				}

				if(cmd.equals("newSimple")){
					String id=String.valueOf(System.currentTimeMillis());

					String thingID=arrays[1];

					String json=getFileContext(arrays[2]);

					RuleEnginePredicate predicate=mapper.readValue(json,RuleEnginePredicate.class);

					SimpleTriggerRecord record=new SimpleTriggerRecord();
					record.setPredicate(predicate);
					record.setId(id);
					record.setRecordStatus(TriggerRecord.StatusType.disable);
					record.setSource(new SimpleTriggerRecord.ThingID());

					engine.createSimpleTrigger(thingID,record);

					triggerID=id;
					System.out.println("create trigger "+triggerID);
					triggerSet.add(id);

				}

				if(cmd.equals("selectTrigger")){
					triggerID=arrays[1];
				}

				if(cmd.equals("listTrigger")){

					triggerSet.forEach((s)->System.out.println("s"));
				}
				if(cmd.equals("setExternalParam")){

					String name=arrays[1];
					String value=arrays[2];

					engine.updateExternalValue("demo",name,value);

				}

				if(cmd.equals("dump")){

					Map<String,Object> result=engine.dumpEngineRuntime();

					String json=mapper.writeValueAsString(result);

					System.out.println(json);

					System.out.println("thing Map:"+thingMap);

				}


			}catch(Exception e){
				e.printStackTrace();
			}

		}


	}

	private static String getFileContext(String name) throws IOException {

		return FileCopyUtils.copyToString(new FileReader("./rule-engine/src/test/resources/console/"+name+".json"));

	}

	private static String readLine()throws IOException{

		char ch=(char)System.in.read();
		StringBuilder sb=new StringBuilder();
		while(ch!='\n'){
			sb.append(ch);
			ch=(char)System.in.read();
		}
		return sb.toString().trim();
	}

	private static ThingStatus getStatus(String params){

		ThingStatus status=new ThingStatus();

		if(!params.contains(",")){
			fillStatus(status, params);
		}else {
			for (String param : params.split(",")) {
				fillStatus(status, param);
			}
		}

		return status;
	}

	private static void fillStatus(ThingStatus status, String param) {
		int idx=param.indexOf("=");

		String key=param.substring(0,idx);
		String value=param.substring(idx+1);

		try{
			Double val=Double.parseDouble(value);
			status.setField(key,val);
		}catch(NumberFormatException e) {
			status.setField(key, value);
		}
	}
}
