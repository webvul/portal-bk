//package com.kii.beehive.business.ruleEngine;
//
//import static junit.framework.TestCase.assertEquals;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.kii.beehive.business.ruleengine.CommandExecuteService;
//import com.kii.beehive.business.ruleengine.ThingCommandForTriggerService;
//import com.kii.beehive.portal.store.StoreServiceTestInit;
//import com.kii.extension.ruleengine.TriggerConditionBuilder;
//import com.kii.extension.ruleengine.store.trigger.CallHttpApi;
//import com.kii.extension.ruleengine.store.trigger.CommandToThing;
//import com.kii.extension.ruleengine.store.trigger.Condition;
//import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
//import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
//import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
//import com.kii.extension.ruleengine.store.trigger.TagSelector;
//import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
//import com.kii.extension.ruleengine.store.trigger.result.ExceptionResponse;
//import com.kii.extension.sdk.entity.thingif.Action;
//import com.kii.extension.sdk.entity.thingif.ThingCommand;
//
//public class TestCommandService extends StoreServiceTestInit {
//
//	@Autowired
//	private ThingCommandForTriggerService cmdService;
//
//
//	@Autowired
//	private ObjectMapper mapper;
//
//	@Test
//	public void doHttpCall(){
//
//
//		CallHttpApi api=new CallHttpApi();
//
//		api.setUrl("http://114.215.196.178:8080/beehive-portal/api/echo");
//		api.setAuthorization("Bearer super_token");
//		api.setContentType("application/json;encode=UTF-8");
//		api.setMethod(CallHttpApi.HttpMethod.POST);
//
//		api.setContent("{\"foo\":\"bar\",\"name\":\"abc\",\"val\":123}");
//
//		Map<String,String> params=new HashMap<>();
//
//
//		TriggerRecord record=new SimpleTriggerRecord();
//		record.setName("name");
//
//		record.addTarget(api);
//
//		cmdService.doCommand(record.getId(),api,params);
//
//		api.setContent("{\"foo\":\"bar\",\"name\":\"${name}\",\"val\":${val}}");
//
//		api.setUrl("http://114.215.196.178:8080/beehive-portal/api/${url}");
//
//		params.put("name","abc");
//		params.put("val","123");
//		params.put("url","echo");
//
//		record.addTarget(api);
//
//		cmdService.doCommand(record,params);
//
//
//
//	}
//
//	@Test
//	public void doCmd(){
//
//		TriggerRecord trigger=new SimpleTriggerRecord();
//		CommandToThing target=new CommandToThing();
//
//		TagSelector sele=new TagSelector();
//		List<Long> things=new ArrayList<>();
//		things.add(1018l);
//		sele.setThingList(things);
//
//		target.setSelector(sele);
//
//		ThingCommand cmd=new ThingCommand();
//		Action power=new Action();
//		power.setField("foo","bar");
//
//		cmd.addAction("foo",power);
//		target.setCommand(cmd);
//
//		List<ExecuteTarget> targets=new ArrayList<>();
//		targets.add(target);
//
//		trigger.setTargets(targets);
//
//		cmdService.doCommand(trigger,new HashMap<>());
//
//	}
//
//
//	@Test
//	public void testJson() throws JsonProcessingException {
//
//		try {
//			try {
//				int i = 10 / 0;
//
//			} catch (Exception e) {
//				throw new IllegalArgumentException(e);
//			}
//		}catch (Exception ex){
//
//			ExceptionResponse resp=new ExceptionResponse(ex);
//
//
//			System.out.println(mapper.writeValueAsString(resp));
//
//		}
//
//
//	}
//
//	@Test
//	public  void testHttpCall() throws IOException {
//
//		String json="{\n" +
//				"\"url\":\"http://114.215.196.178:8080/beehive-portal/api/oauth2/registUser\",\n" +
//				"\"contentType\":\"application/json\",\n" +
//				"\"content\":\"{\\\"userName\\\":\\\"vivi2\\\",\\\"password\\\":\\\"1qaz2wsx\\\",\\\"displayNamel\\\":\\\"vivi\\\"}\",\n" +
//				"\"method\":\"POST\",\n" +
//				"\"type\":\"HttpApiCall\"\n" +
//				"}";
//
//		CallHttpApi  api=mapper.readValue(json,CallHttpApi.class);
//
//
//		assertEquals(api.getContentType(),"application/json");
//
//		assertEquals(api.getAuthorization(),null);
//
//		SimpleTriggerRecord trigger=new SimpleTriggerRecord();
//		trigger.addTarget(api);
//
//		cmdService.doCommand(trigger,new HashMap<>());
//
//		System.in.read();
//	}
//
//	@Test
//	public void doDelay(){
//
//		SimpleTriggerRecord trigger=new SimpleTriggerRecord();
//		CommandToThing target=new CommandToThing();
//
//		TagSelector sele=new TagSelector();
//		List<Long> things=new ArrayList<>();
//		things.add(1018l);
//		sele.setThingList(things);
//
//		target.setDelay("1000");
//		target.setDoubleCheck(true);
//
//		target.setSelector(sele);
//
//		ThingCommand cmd=new ThingCommand();
//		Action power=new Action();
//		power.setField("foo","bar");
//
//		cmd.addAction("foo",power);
//		target.setCommand(cmd);
//
//		List<ExecuteTarget> targets=new ArrayList<>();
//		targets.add(target);
//
//		trigger.setTargets(targets);
//
//		CallHttpApi api=new CallHttpApi();
//
//		api.setUrl("http://114.215.196.178:8080/beehive-portal/api/echo");
//		api.setAuthorization("Bearer super_token");
//		api.setContentType("application/json;encode=UTF-8");
//		api.setMethod(CallHttpApi.HttpMethod.POST);
//
//		api.setContent("{\"foo\":\"bar\",\"name\":\"abc\",\"val\":123}");
//
//		cmd.addAction("foo",power);
//
//		trigger.addTarget(api);
//
//		SimpleTriggerRecord.ThingID thingID=new SimpleTriggerRecord.ThingID();
//		thingID.setThingID(1052);
//
//		trigger.setSource(thingID);
//
//
//		RuleEnginePredicate predicate=new RuleEnginePredicate();
//		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
//		predicate.setCondition(condition);
//
//		trigger.setPredicate(predicate);
//
//		Map map=new HashMap<>();
//		map.put("delay_0","1000");
//
//		cmdService.doCommand(trigger,map);
//
//	}
//
//}
