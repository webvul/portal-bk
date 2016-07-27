package com.kii.extension.ruleengine.console;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;


public class RuleEngineConsole {



	private EngineService  engine;

	private ObjectMapper  mapper;

	private String triggerID=null;

	private Set<String> triggerSet=new HashSet<>();

	Map<String,Set<String>>  tagMap=new HashMap<>();


	public RuleEngineConsole(ClassPathXmlApplicationContext context){
		 engine=context.getBean(EngineService.class);

		 mapper=context.getBean(ObjectMapper.class);




		Set<String> ths=new HashSet<>();
		ths.add("a");
		ths.add("b");
		ths.add("c");
		ths.add("d");


		tagMap.put("comm",ths);
		tagMap.put("one",ths);
		tagMap.put("two",ths);
		tagMap.put("three", Collections.singleton("c"));
		tagMap.put("four", Collections.singleton("d"));
		tagMap.put("five",ths);
		tagMap.put("six",ths);
		tagMap.put("seven", Collections.singleton("b"));
		tagMap.put("eight", Collections.singleton("a"));


	}


	public void init(){


		engine.updateExternalValue("demo","one",111);

		engine.updateExternalValue("demo","two",222);

		List<ThingStatusInRule> statusList=new ArrayList<>();
		statusList.add(getStatusInRule("a","foo=100,bar=-10"));
		statusList.add(getStatusInRule("b","foo=10,bar=-100"));
		statusList.add(getStatusInRule("c","foo=-10,bar=10"));
		statusList.add(getStatusInRule("d","foo=-100,bar=100"));

		engine.initThingStatus(statusList);

	}

	public void loadAll(){

		File triggerPath=new File("./rule-engine/src/test/resources/console");

		File[] fileArray=triggerPath.listFiles((dir, name) -> {
			return name.endsWith(".json");
		});

		for(File file:fileArray){

			try {
				String json=FileCopyUtils.copyToString(new FileReader(file));

				addTrigger(json);
			} catch (IOException e) {
				e.printStackTrace();
			}

		};
	}


	public void doMsgCycle(String cmd,String[] arrays){



		switch(cmd) {

			case "loadAll":
				loadAll();
				break;

			case "exit":
				System.exit(0);


			case "setStatus":
				String params = arrays[2];

				ThingStatus status = getStatus(params);

				engine.updateThingStatus(arrays[1], status, new Date());
				break;
			case "enable":
				engine.enableTrigger(triggerID);
				break;
			case "disable":
				engine.disableTrigger(triggerID);
				break;
			case "remove":
				engine.removeTrigger(triggerID);
				break;
			case "setThingCol":

				String summaryID = arrays[1];
				String[] things = arrays[2].split(",");

				Set<String> set = new HashSet<>();
				set.addAll(Arrays.asList(things));

				tagMap.put(summaryID, set);
				break;
			case "newTrigger":
				try {
					String jsonTrigger = getFileContext(arrays[1]);

					addTrigger(jsonTrigger);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case "selectTrigger":
				triggerID = arrays[1];
				break;
			case "listTrigger":
				triggerSet.forEach((s) -> System.out.println("s"));
				break;
			case "setExt":
				String name = arrays[1];
				String value = arrays[2];

				engine.updateExternalValue("demo", name, value);
				break;
			case "dump":

				Map<String, Object> result = engine.dumpEngineRuntime();

				try {
					String json = mapper.writeValueAsString(result);

					System.out.println(json);

					System.out.println("thing Map:" + tagMap);

				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}

				break;
			default:
		}

	}

	public static void main(String[] argc){

		ClassPathXmlApplicationContext  context=new ClassPathXmlApplicationContext("classpath:ruleTestContext.xml");

		RuleEngineConsole console=new RuleEngineConsole(context);

		console.init();

		System.out.println(">>> input command\n");


		while(true){


			try {

				String input = readLine();

				if(StringUtils.isEmpty(input)){
					continue;
				}

				String[] arrays= StringUtils.tokenizeToStringArray(input," ");

				String cmd=arrays[0];

				console.doMsgCycle(cmd,arrays);

			}catch(Exception e){
				e.printStackTrace();
			}

		}


	}


	private  void addTrigger(String json) throws IOException {


		TriggerRecord record=mapper.readValue(json,TriggerRecord.class);

		String id=String.valueOf(System.currentTimeMillis());

		record.setId(id);
		record.setRecordStatus(TriggerRecord.StatusType.enable);

		switch(record.getType()) {
			case Simple:
				SimpleTriggerRecord rec=(SimpleTriggerRecord)record;
				char thID= (char) ((int)rec.getSource().getThingID()+'a');

				engine.createSimpleTrigger(String.valueOf(thID),rec);

				break;
			case Multiple:

				engine.createMultipleSourceTrigger((MultipleSrcTriggerRecord) record, tagMap);
				break;
			case Group:
				GroupTriggerRecord recGroup=(GroupTriggerRecord)record;

				engine.createGroupTrigger(recGroup, tagMap.get(recGroup.getSource().getTagList().iterator().next()));

				break;
			case Summary:

				SummaryTriggerRecord recSummary=(SummaryTriggerRecord)record;

				engine.createSummaryTrigger(recSummary, tagMap);

				break;
		}
		triggerID=id;

		System.out.println("create trigger "+triggerID);

		triggerSet.add(id);
	}

	private  String getFileContext(String name) throws IOException {

		return FileCopyUtils.copyToString(new FileReader("./rule-engine/src/test/resources/console/"+name+".json"));

	}

	private  static String readLine()throws IOException{

		char ch=(char)System.in.read();
		StringBuilder sb=new StringBuilder();
		while(ch!='\n'){
			sb.append(ch);
			ch=(char)System.in.read();
		}
		return sb.toString().trim();
	}

	private  ThingStatusInRule  getStatusInRule(String thingID,String params){

		ThingStatusInRule status=new ThingStatusInRule(thingID);

		ThingStatus  s=getStatus(params);
		status.setValues(s.getFields());
		status.setCreateAt(new Date());

		return status;
	}

	private  ThingStatus getStatus(String params){

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

	private  void fillStatus(ThingStatus status, String param) {
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
