package com.kii.beehive.business.ruleEngine.console;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.ruleengine.CommandExecuteService;
import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SingleThing;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.TriggerGroupPolicy;
import com.kii.extension.ruleengine.store.trigger.groups.TriggerGroupPolicyType;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;


public class TriggerMangerConsole {

	public static void main(String[] argc){

		ClassPathXmlApplicationContext  context=new ClassPathXmlApplicationContext("classpath:com/kii/beehive/portal/store/testStoreServiceContext.xml");

		TriggerManager engine=context.getBean(TriggerManager.class);

//		RuleEngineInputCallback callback=context.getBean(RuleEngineInputCallback.class);

		CommandExecuteService execService=context.getBean(CommandExecuteService.class);

//		execService.disable();

		ObjectMapper mapper=context.getBean(ObjectMapper.class);

		System.out.println(">>> input command\n");

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

				if(cmd.equals("updateStatus")){
					String params=arrays[2];

					ThingStatus status=getStatus(params);

//					callback.onEventFire("",status,arrays[1],new Date());

				}
				if(cmd.equals("enable")){

					String triggerID=arrays[1];

					engine.enableTrigger(triggerID);
				}
				if(cmd.equals("disable")){
					String triggerID=arrays[1];
					engine.disableTrigger(triggerID);
				}
				if(cmd.equals("remove")){
					String triggerID=arrays[1];
					engine.deleteTrigger(triggerID);
				}
//				if(cmd.equals("update")){
//					String triggerID=arrays[1];
//					String[] things=arrays[2].split(",");
//
//					engine.changeThingsInTrigger(triggerID,new HashSet(Arrays.asList(things)));
//
//				}
//				if(cmd.equals("updateSummary")){
//					String triggerID=arrays[1];
//					String summaryName=arrays[2];
//					String[] things=arrays[3].split(",");
//
//					engine.changeThingsInSummary(triggerID,summaryName,new HashSet(Arrays.asList(things)));
//				}
				if(cmd.equals("newSimple")){
					String thingID=arrays[1];

					String json=getFileContext(arrays[2]);

					RuleEnginePredicate predicate=mapper.readValue(json,RuleEnginePredicate.class);

					SimpleTriggerRecord record=new SimpleTriggerRecord();
					record.setPredicate(predicate);
					record.setRecordStatus(TriggerRecord.StatusType.disable);
					record.setSource(new SingleThing());
					record.getSource().setThingID(Long.parseLong(thingID));
					List<ExecuteTarget> targets = getTargets();
					record.setTargets(targets);

					engine.createTrigger(record);
				}

				if(cmd.equals("createGroup")){
					String triggerID=arrays[1];
					String[] thingIDs=arrays[2].split(",");

					String json=getFileContext(arrays[3]);

					RuleEnginePredicate predicate=mapper.readValue(json,RuleEnginePredicate.class);

					GroupTriggerRecord record=new GroupTriggerRecord();
					record.setRecordStatus(TriggerRecord.StatusType.enable);
					record.setPredicate(predicate);
					record.setId(triggerID);


					String[] policys=arrays[4].split(":");

					String policyType=policys[0];
					int number=Integer.parseInt(policys[1]);
					TriggerGroupPolicy policy=new TriggerGroupPolicy();
					policy.setCriticalNumber(number);
					policy.setGroupPolicy(TriggerGroupPolicyType.valueOf(policyType));

					record.setPolicy(policy);

					engine.createTrigger(record);
				}

				if(cmd.equals("dump")){

					Map<String,Object> result=engine.getRuleEngingDump();

					String json=mapper.writeValueAsString(result);

					System.out.println(json);


				}

				if(cmd.equals("init")){

					engine.init();
				}


			}catch(Exception e){
				e.printStackTrace();
			}

		}


	}

	private static List<ExecuteTarget> getTargets() {
		List<ExecuteTarget> targets=new ArrayList<>();
		CommandToThing target=new CommandToThing();
		ThingCommand thingCmd=new ThingCommand();
		Action action=new Action();
		action.setField("power","ON");
		thingCmd.addAction("power",action);
		target.setCommand(thingCmd);
		targets.add(target);
		return targets;
	}

	private static String getFileContext(String name) throws IOException {

		return FileCopyUtils.copyToString(new FileReader("./store-service/src/test/resources/console/"+name+".json"));

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
			for (String param : StringUtils.split(params, ",")) {
				fillStatus(status, param);
			}
			;
		}

		return status;
	}

	private static void fillStatus(ThingStatus status, String param) {
		int idx=param.indexOf("=");

		String key=param.substring(0,idx);
		String value=param.substring(idx+1);

		status.setField(key,value);
	}
}
