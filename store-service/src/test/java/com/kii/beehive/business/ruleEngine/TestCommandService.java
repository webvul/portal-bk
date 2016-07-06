package com.kii.beehive.business.ruleEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.ruleengine.CommandExecuteService;
import com.kii.beehive.portal.store.StoreServiceTestInit;
import com.kii.extension.ruleengine.store.trigger.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class TestCommandService extends StoreServiceTestInit {

	@Autowired
	private CommandExecuteService cmdService;


	@Test
	public void doHttpCall(){


		CallHttpApi api=new CallHttpApi();

		api.setUrl("http://114.215.196.178:8080/beehive-portal/api/echo");
		api.setAuthorization("Bearer super_token");
		api.setContentType("application/json;encode=UTF-8");
		api.setMethod(CallHttpApi.HttpMethod.POST);

		api.setContent("{\"foo\":\"bar\",\"name\":\"abc\",\"val\":123}");

		Map<String,String> params=new HashMap<>();


		TriggerRecord record=new SimpleTriggerRecord();
		record.setName("name");

		record.addTarget(api);

		cmdService.doCommand(record,params);

		api.setContent("{\"foo\":\"bar\",\"name\":\"${name}\",\"val\":${val}}");

		api.setUrl("http://114.215.196.178:8080/beehive-portal/api/${url}");

		params.put("name","abc");
		params.put("val","123");
		params.put("url","echo");

		record.addTarget(api);

		cmdService.doCommand(record,params);



	}

	@Test
	public void doCmd(){

		TriggerRecord trigger=new SimpleTriggerRecord();
		CommandToThing target=new CommandToThing();

		TagSelector sele=new TagSelector();
		List<Long> things=new ArrayList<>();
		things.add(1018l);
		sele.setThingList(things);

		target.setSelector(sele);
		
		ThingCommand cmd=new ThingCommand();
		Action power=new Action();
		power.setField("foo","bar");

		cmd.addAction("foo",power);
		target.setCommand(cmd);

		List<ExecuteTarget> targets=new ArrayList<>();
		targets.add(target);

		trigger.setTarget(targets);

		cmdService.doCommand(trigger,new HashMap<>());

	}

}
