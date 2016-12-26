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
import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.SchedulerException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.drools.entity.BusinessObjInRule;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.ThingCollectSource;
import com.kii.extension.ruleengine.store.trigger.ThingSource;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;


public class RuleEngineConsole {



	private BeehiveTriggerService service;

	private ObjectMapper  mapper;

	private String triggerID=null;


	private Map<String,Boolean> triggerMap=new HashMap<>();

	private Map<String,Set<String>> tagMap=new HashMap<>();


	private ScheduledExecutorService executeService=new ScheduledThreadPoolExecutor(10);
	
	private AtomicBoolean  taskSign=new AtomicBoolean(false);

	public RuleEngineConsole(ClassPathXmlApplicationContext context){

		 mapper=context.getBean(ObjectMapper.class);

		mapper.configure(SerializationFeature.INDENT_OUTPUT,true);


		service=context.getBean(BeehiveTriggerService.class);


		Set<String> ths=new HashSet<>();
		ths.add("0");
		ths.add("1");
		ths.add("2");
		ths.add("3");


		tagMap.put("comm",ths);
		tagMap.put("one",ths);
		tagMap.put("two",ths);
		tagMap.put("three", Collections.singleton("2"));
		tagMap.put("four", Collections.singleton("3"));
		tagMap.put("five",ths);
		tagMap.put("six",ths);
		tagMap.put("seven", Collections.singleton("1"));
		tagMap.put("eight", Collections.singleton("0"));


	}

	public static class Entry{

		private String v;

		private Map<String,Object> map=new HashMap<>();

		private String[] a;

		private int n;

		public int getNum() {
			return n;
		}

		public void setNum(int n) {
			this.n = n;
		}

		public String getVal() {
			return v;
		}

		public void setVal(String v) {
			this.v = v;
		}

		public Map<String, Object> getMap() {
			return map;
		}

		public void setMap(Map<String, Object> map) {
			this.map = map;
		}

		public String[] getArr() {
			return a;
		}

		public void setArr(String[] a) {
			this.a = a;
		}
	}

	public void init() throws IOException, SchedulerException {


		service.enterInit();

		initExternal();


		List<BusinessObjInRule> statusList=new ArrayList<>();
		statusList.add(getStatusInRule("0","foo=230,bar=150"));
		statusList.add(getStatusInRule("1","foo=230,bar=50"));
		statusList.add(getStatusInRule("2","foo=-10,bar=10"));
		statusList.add(getStatusInRule("3","foo=-100,bar=100"));
		
		
		statusList.forEach(s->{
			BusinessDataObject obj=new BusinessDataObject(s.getThingID(),null, BusinessObjType.Thing);
			obj.setData(s.getValues());
			
			service.updateBusinessData(obj);
		});

		service.leaveInit();

		executeService.scheduleAtFixedRate(()->{

			if(taskSign.get()) {
				Map<String, Object> map = new HashMap<>();
				map.put("time", System.currentTimeMillis() % 100);
				
				BusinessDataObject obj=new BusinessDataObject("4",null, BusinessObjType.Thing);
				obj.setData(map);
				service.updateBusinessData(obj);
			}

		},3, 30,TimeUnit.SECONDS);

	}

	public void initExternal() {
		service.updateExternalValue("demo","one",111);

		service.updateExternalValue("demo","two",222);

//       "express":"ml.score('one',$p{1},$p{2})>$e{demo.map[c].d} "

		Entry entry=new Entry();
		entry.setVal("value");
		entry.setNum(-100);


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
				
			case "stop":
				taskSign.set(false);
				break;
				
			case "restore":
				taskSign.set(true);
				break;
			
			case "setStatus":
				String params = arrays[2];

				int num=1;
				if(arrays.length>3){
					num=Integer.parseInt(arrays[3]);
				};

				ThingStatus status = getStatus(params);

				for(int i=0;i<num;i++) {
					BusinessDataObject obj=new BusinessDataObject(arrays[1],null, BusinessObjType.Thing);
					obj.setData(status.getFields());
					service.updateBusinessData(obj);
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

				thingMap.put("comm",Collections.singleton(rec.getSource().getThingID()));

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

		service.addTriggerToEngine(record,thingMap,true );

		System.out.println("create trigger "+triggerID);

		triggerMap.put(triggerID,true);
	}


	private Map<String,Set<String>> getThingMap(SummaryTriggerRecord  record ){


		Map<String,Set<String>>  thingMap=new HashMap<>();

		record.getSummarySource().forEach( (k,v)->{

			Set<String> thSet=new HashSet<>();
			if(v.getSource().getThingList().isEmpty()){
				v.getSource().getSelector().getTagList().forEach(tag->{
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

		ThingCollectSource sele=record.getSource();

		Set<String> thSet=new HashSet<>();
			if(sele.getThingList().isEmpty()){
				sele.getSelector().getTagList().forEach(tag->{
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
				ThingCollectSource g=((GroupSummarySource)v).getSource();

				if(g.getThingList().isEmpty()){
					g.getSelector().getTagList().forEach(tag->{
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

				String th=((ThingSource)v).getBusinessID();
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

	private BusinessObjInRule getStatusInRule(String thingID, String params){

		BusinessObjInRule status=new BusinessObjInRule(thingID);

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
