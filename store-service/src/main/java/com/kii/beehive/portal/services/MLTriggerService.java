package com.kii.beehive.portal.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.entitys.MLTriggerCombine;
import com.kii.beehive.portal.helper.HttpClient;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@Component
public class MLTriggerService {
	
	private Logger log= LoggerFactory.getLogger(MLTriggerService.class);
	
	
	
	@Autowired
	private TriggerManager triggerOper;
	
	@Autowired
	private RuleGeneral general;
	
	@Autowired
	private ObjectMapper mapper;
	
	
	
	private String mlServiceUrl;
	
	private  static final String PARAM_NAME="ml_output";
	
	
	private ScheduledExecutorService  schedule= Executors.newScheduledThreadPool(10);
	
	@Autowired
	private HttpClient  http;
	
	private void addBusinessParamPull(String mlTaskID,int interval,String triggerID){


		schedule.scheduleAtFixedRate(() -> {

//			HttpUriRequest request=new HttpGet(""+mlTaskID);
//
//			String response=http.executeRequest(request);

			Map<String,Object> demo=new HashMap<>();
			demo.put("foo",12);
			demo.put("bar",30);
			
			try {
				
				String response=mapper.writeValueAsString(demo);
				
				Map<String,Object> map=mapper.readValue(response, Map.class);
				
				triggerOper.updateTriggerInstData(triggerID,PARAM_NAME,map);

			} catch (IOException e) {
				log.warn("get ML data fail:task id"+mlTaskID,e.getMessage());
			}


		}, 0,interval, TimeUnit.MINUTES);
	}
	

	public  void  createTriggerWithML(MLTriggerCombine combine){
		
		TriggerRecord businessTrigger=combine.getBusinessTrigger();


		String mlExpress= addParamPrefix(general.convertCondition(combine.getMlCondition()));
		
		RuleEnginePredicate predicate=businessTrigger.getPredicate();
		
		String originExpress=predicate.getExpress();

		if(predicate.getCondition()!=null){
			originExpress=general.convertCondition(predicate.getCondition());
		}

		String newExpress= originExpress + " and ( "+ mlExpress +" ) ";

		predicate.setExpress(newExpress);
		predicate.setCondition(null);

		businessTrigger.setPredicate(predicate);

		String triggerID=triggerOper.createTrigger(businessTrigger).getTriggerID();

		addBusinessParamPull(combine.getMlTaskID(),10,triggerID);

		
	}
	
	public  String addParamPrefix(String express){
		StringBuffer sb=new StringBuffer();
		
		Pattern pattern= Pattern.compile("\\$p(\\:\\w)?\\{([^\\}]+)\\}");
		
		Matcher matcher=pattern.matcher(express);
		
		while(matcher.find()) {
			
			int start=matcher.start(2);
			
			int base=matcher.start();
			
			String str=matcher.group();
			
			StringBuffer buf=new StringBuffer(str);
			buf.insert(start-base,PARAM_NAME+".");
			
			buf.setCharAt(1,'t');
			
			matcher.appendReplacement(sb,Matcher.quoteReplacement(buf.toString()));
		}
		matcher.appendTail(sb);
		
		return sb.toString();
	}

}
