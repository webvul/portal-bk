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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.quartz.SchedulerException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.ThingSource;
import com.kii.extension.sdk.entity.thingif.ThingStatus;


public class RuleEngineConsole {



	private BeehiveTriggerService service;

	private ObjectMapper  mapper;

	private String triggerID=null;


	private Map<String,Boolean> triggerMap=new HashMap<>();

	private Map<String,Set<String>> tagMap=new HashMap<>();


	private ScheduledExecutorService executeService=new ScheduledThreadPoolExecutor(10);

	public RuleEngineConsole(ClassPathXmlApplicationContext context){

		 mapper=context.getBean(ObjectMapper.class);


		service=context.getBean(BeehiveTriggerService.class);


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

	public static class Entry{

		private String v;

		private Map<String,Object> map=new HashMap<>();

		private String[] a;

		private int n;

		public int getN() {
			return n;
		}

		public void setN(int n) {
			this.n = n;
		}

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}

		public Map<String, Object> getMap() {
			return map;
		}

		public void setMap(Map<String, Object> map) {
			this.map = map;
		}

		public String[] getA() {
			return a;
		}

		public void setA(String[] a) {
			this.a = a;
		}
	}

	public void init() throws IOException, SchedulerException {


		service.enterInit();

		initExternal();


		List<ThingStatusInRule> statusList=new ArrayList<>();
		statusList.add(getStatusInRule("a","foo=230,bar=150"));
		statusList.add(getStatusInRule("b","foo=230,bar=50"));
		statusList.add(getStatusInRule("c","foo=-10,bar=10"));
		statusList.add(getStatusInRule("d","foo=-100,bar=100"));
		statusList.forEach(s->service.updateThingStatus(s.getThingID(),s.getValues()));

		service.leaveInit();

		executeService.scheduleAtFixedRate(()->{

			Map<String,Object> map=new HashMap<>();
			map.put("time",System.currentTimeMillis()%100);
			service.updateThingStatus("e", map);


		},3, 30,TimeUnit.SECONDS);

	}

	public void initExternal() {
		service.updateExternalValue("demo","one",111);

		service.updateExternalValue("demo","two",222);

//       "express":"ml.score('one',$p{1},$p{2})>$e{demo.map[c].d} "

		Entry entry=new Entry();
		entry.setV("value");
		entry.setN(100);


		Map<String,Object> map=new HashMap<>();
		map.put("c",entry);

		service.updateExternalValue("demo","map",map);
	}

	public void loadAll(){

		File triggerPath=new File("./rule-engine/src/test/resources/console");

		File[] fileArray=triggerPath.listFiles((dir, name) -> {
			return name.endsWith(".json");
		});

		for(File file:fileArray){

			try {
				String json=FileCopyUtils.copyToString(new FileReader(file));

				addTrigger(json,file.getName());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(file.getName());
			}

		};
	}


	public void doMsgCycle(String cmd,String[] arrays) throws SchedulerException {



		switch(cmd) {

			case "loadAll":
				loadAll();
				break;

			case "exit":
				System.exit(0);


			case "setStatus":
				String params = arrays[2];

				int num=1;
				if(arrays.length>3){
					num=Integer.parseInt(arrays[3]);
				};

				ThingStatus status = getStatus(params);

				for(int i=0;i<num;i++) {
					service.updateThingStatus(arrays[1], status.getFields());
				}
				break;
			case "remove":
				if(triggerMap.get(triggerID)) {
					service.removeTrigger(triggerID);
				}
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

					addTrigger(jsonTrigger,arrays[1]);
				} catch (IOException|SchedulerException e) {
					e.printStackTrace();
				}
				break;

			case "selectTrigger":
				triggerID = arrays[1];
				break;

			case "listTrigger":
				triggerMap.keySet().forEach((s) -> System.out.println(s));
				break;
			case "setExt":
				String name = arrays[1];
				String value = arrays[2];

				service.updateExternalValue("demo", name, value);
				break;

			case "dump":

				Map<String, Object> result = service.getRuleEngingDump(null);

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

		ClassPathXmlApplicationContext  context=new ClassPathXmlApplicationContext("classpath:ruleConsoleCtx.xml");

		RuleEngineConsole console=new RuleEngineConsole(context);


		try {
			console.init();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (SchedulerException e) {
			e.printStackTrace();
			System.exit(-1);

		}

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


	private  void addTrigger(String json,String name) throws IOException, SchedulerException {


		TriggerRecord record=mapper.readValue(json,TriggerRecord.class);

		String id=String.valueOf(System.currentTimeMillis())+name;

		record.setId(id);

		triggerID=id;
		record.setRecordStatus(TriggerRecord.StatusType.enable);

		Map<String,Set<String>>  thingMap=new HashMap<>();

		switch(record.getType()) {
			case Simple:
				SimpleTriggerRecord rec=(SimpleTriggerRecord)record;
				char thID= (char) ( (int)rec.getSource().getThingID()-1+'a');

				String thingID=String.valueOf(thID);

				thingMap.put("comm",Collections.singleton(thingID));

				break;
			case Multiple:

				thingMap= getThingMap((MultipleSrcTriggerRecord)record);
				break;
			case Group:
				GroupTriggerRecord recGroup=(GroupTriggerRecord)record;

				thingMap=getThingMap(recGroup );

				break;
			case Summary:

				SummaryTriggerRecord recSummary=(SummaryTriggerRecord)record;

				thingMap=getThingMap(recSummary);

				break;
		}

		service.addTriggerToEngine(record,thingMap);

		System.out.println("create trigger "+triggerID);

		triggerMap.put(triggerID,true);
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


	private Map<String,Set<String>> getThingMap(GroupTriggerRecord  record ){

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

		return Collections.singletonMap("comm",thSet);
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
