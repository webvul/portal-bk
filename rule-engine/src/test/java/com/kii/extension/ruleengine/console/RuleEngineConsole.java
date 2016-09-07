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

import org.quartz.SchedulerException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerValidPeriod;
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.ThingSource;
import com.kii.extension.sdk.entity.thingif.ThingStatus;


public class RuleEngineConsole {



	private EngineService  engine;

	private ScheduleService  schedule;

	private ObjectMapper  mapper;

	private String triggerID=null;

	private Set<String> triggerSet=new HashSet<>();

	private Map<String,Set<String>> tagMap=new HashMap<>();


	public RuleEngineConsole(ClassPathXmlApplicationContext context){
		 engine=context.getBean(EngineService.class);

		 mapper=context.getBean(ObjectMapper.class);


		schedule=context.getBean(ScheduleService.class);


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
		statusList.add(getStatusInRule("a","foo=230,bar=150"));
		statusList.add(getStatusInRule("b","foo=230,bar=50"));
		statusList.add(getStatusInRule("c","foo=-10,bar=10"));
		statusList.add(getStatusInRule("d","foo=-100,bar=100"));

		engine.initThingStatus(statusList);

//		schedule.startSchedule();

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
			} catch (IOException|SchedulerException e) {
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
			case "remove":
				engine.removeTrigger(triggerID);
				schedule.removeManagerTaskForSchedule(triggerID);
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
				} catch (IOException|SchedulerException e) {
					e.printStackTrace();
				}
				break;

			case "selectTrigger":
				triggerID = arrays[1];
				break;
			case "listTrigger":
				triggerSet.forEach((s) -> System.out.println(s));
				break;
			case "setExt":
				String name = arrays[1];
				String value = arrays[2];

				engine.updateExternalValue("demo", name, value);
				break;
			case "dump":

				Map<String, Object> result = engine.dumpEngineRuntime();

				result.put("schedule",schedule.dump());

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

			}catch(IOException e){
				e.printStackTrace();
				break;
			}catch(Exception ex){
				ex.printStackTrace();
			}

		}


	}


	private  void addTrigger(String json) throws IOException, SchedulerException {


		TriggerRecord record=mapper.readValue(json,TriggerRecord.class);

		String id=String.valueOf(System.currentTimeMillis());

		record.setId(id);

		triggerID=id;
		record.setRecordStatus(TriggerRecord.StatusType.enable);

		TriggerValidPeriod period=record.getPreparedCondition();
		if(period!=null){
			//ctrl enable sign by schedule.
			record.setRecordStatus(TriggerRecord.StatusType.disable);
		}

		switch(record.getType()) {
			case Simple:
				SimpleTriggerRecord rec=(SimpleTriggerRecord)record;
				char thID= (char) ((int)rec.getSource().getThingID()+'a');

				engine.createSimpleTrigger(String.valueOf(thID),rec);

				break;
			case Multiple:

				engine.createMultipleSourceTrigger((MultipleSrcTriggerRecord) record, getThingMap((MultipleSrcTriggerRecord)record));
				break;
			case Group:
				GroupTriggerRecord recGroup=(GroupTriggerRecord)record;

				engine.createGroupTrigger(recGroup,getThingMap(recGroup) );

				break;
			case Summary:

				SummaryTriggerRecord recSummary=(SummaryTriggerRecord)record;

				engine.createSummaryTrigger(recSummary, getThingMap(recSummary));

				break;
		}

		if(period!=null) {
			schedule.addManagerTask(triggerID, period);
		}

		triggerID=id;

		System.out.println("create trigger "+triggerID);

		triggerSet.add(id);
	}


	private Map<String,Set<String>> getThingMap(SummaryTriggerRecord  record ){


		Map<String,Set<String>>  thingMap=new HashMap<>();

		record.getSummarySource().forEach( (k,v)->{

			Set<String> thSet=new HashSet<>();
			if(v.getSource().getThingList().isEmpty()){
				v.getSource().getTagList().forEach(tag->{
					thSet.addAll(tagMap.get(tag));
				});
			}else{

				v.getSource().getThingList().forEach((l)->{
						String th= String.copyValueOf(new char[]{(char) ('a'+l)});
						thSet.add(th);
					}
				);
			}
			thingMap.put(k,thSet);

		});
		return thingMap;
	}


	private Set<String> getThingMap(GroupTriggerRecord  record ){

		TagSelector sele=record.getSource();

		Set<String> thSet=new HashSet<>();
			if(sele.getThingList().isEmpty()){
				sele.getTagList().forEach(tag->{
					thSet.addAll(tagMap.get(tag));
				});
			}else{

				sele.getThingList().forEach((l)->{
							String th= String.copyValueOf(new char[]{(char) ('a'+l)});
							thSet.add(th);
						}
				);
			}

		return thSet;
	}

	private Map<String,Set<String>> getThingMap(MultipleSrcTriggerRecord  record ){


		Map<String,Set<String>>  thingMap=new HashMap<>();

		record.getSummarySource().forEach( (k,v)->{
			Set<String> thSet=new HashSet<>();

			if( v instanceof GroupSummarySource){
				TagSelector  g=((GroupSummarySource)v).getSource();

				if(g.getThingList().isEmpty()){
					g.getTagList().forEach(tag->{
						thSet.addAll(tagMap.get(tag));
					});
				}else{

					g.getThingList().forEach((l)->{
								String th= String.copyValueOf(new char[]{(char) ('a'+l)});
								thSet.add(th);
							}
					);
				}
			}else{

				String th=((ThingSource)v).getThingID();
				thSet.add(th);

			}
			thingMap.put(k,thSet);

		});
		return thingMap;
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
