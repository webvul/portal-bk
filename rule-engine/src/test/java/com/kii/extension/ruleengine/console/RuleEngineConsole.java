package com.kii.extension.ruleengine.console;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicy;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicyType;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;


public class RuleEngineConsole {

	public static void main(String[] argc){

		ClassPathXmlApplicationContext  context=new ClassPathXmlApplicationContext("classpath:./ruleTestContext.xml");

		EngineService engine=context.getBean(EngineService.class);

		ObjectMapper mapper=context.getBean(ObjectMapper.class);
		while(true){


			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

				String input = reader.readLine();

				if(StringUtils.isEmpty(input)){
					continue;
				}

				String[] arrays= StringUtils.split(input," ");

				String cmd=arrays[0];

				if(cmd.equals("exit")){
					System.exit(0);
				}

				if(cmd.equals("updateStatus")){
					String params=arrays[2];

					ThingStatus status=getStatus(params);

					engine.updateThingStatus(arrays[1],status,new Date());

				}
				if(cmd.equals("enableTrigger")){

					String triggerID=arrays[1];

					engine.enableTrigger(triggerID);
				}
				if(cmd.equals("disableTrigger")){
					String triggerID=arrays[1];
					engine.disableTrigger(triggerID);
				}
				if(cmd.equals("removeTrigger")){
					String triggerID=arrays[1];
					engine.removeTrigger(triggerID);
				}
				if(cmd.equals("updateTrigger")){
					String triggerID=arrays[1];
					String[] things=arrays[2].split(",");

					engine.changeThingsInTrigger(triggerID,new HashSet(Arrays.asList(things)));

				}
				if(cmd.equals("updateSummary")){
					String triggerID=arrays[1];
					String summaryName=arrays[2];
					String[] things=arrays[3].split(",");

					engine.changeThingsInSummary(triggerID,summaryName,new HashSet(Arrays.asList(things)));
				}
				if(cmd.equals("createSimpleTrigger")){
					String triggerID=arrays[1];
					String thingID=arrays[2];

					String json=FileCopyUtils.copyToString(new FileReader(arrays[3]));

					RuleEnginePredicate predicate=mapper.readValue(json,RuleEnginePredicate.class);

					SimpleTriggerRecord record=new SimpleTriggerRecord();
					record.setRecordStatus(TriggerRecord.StatusType.enable);
					record.setPredicate(predicate);
					record.setId(triggerID);
					record.setSource(new SimpleTriggerRecord.ThingID());

					engine.createSimpleTrigger(thingID,record);
				}

				if(cmd.equals("createGroupTrigger")){
					String triggerID=arrays[1];
					String[] thingIDs=arrays[2].split(",");

					String json=FileCopyUtils.copyToString(new FileReader(arrays[3]));



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

					engine.createGroupTrigger(Arrays.asList(thingIDs),record);
				}


			}catch(Exception e){
				e.printStackTrace();
			}

		}


	}

	private static ThingStatus getStatus(String params){

		ThingStatus status=new ThingStatus();

		for(String param:StringUtils.split(params,",")){
			int idx=param.indexOf("=");

			String key=param.substring(0,idx);
			String value=param.substring(idx+1);

			status.setField(key,value);
		};

		return status;
	}
}
