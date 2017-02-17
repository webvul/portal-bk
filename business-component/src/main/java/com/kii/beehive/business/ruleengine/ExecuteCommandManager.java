package com.kii.beehive.business.ruleengine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.ruleengine.entitys.BusinessFunctionParam;
import com.kii.beehive.business.ruleengine.entitys.ThingCommandExecuteParam;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.MethodTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

@Component
public class ExecuteCommandManager {


	private Logger log= LoggerFactory.getLogger(ExecuteCommandManager.class);


	@Autowired
	private ThingIFInAppService thingIFService;


	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private AppInfoManager appInfoManager;
	
	
	@Autowired
	private ApplicationContext context;

	public Map<Long,String> executeCommand(ThingCommandExecuteParam commandToThing){
		
		Map<Long,String>  resultMap=new HashMap<>();
		
		Set<GlobalThingInfo> thSet=new HashSet<>();
		
		if(commandToThing.getThingList().isEmpty()) {
			thSet=thingTagService.getThingInfos(commandToThing.getSelector());
		}else{
			thSet = thingTagService.getThingInfosByIDs(commandToThing.getThingList(), commandToThing.getUserID());
		}
		
		if(thSet.isEmpty()){
			return resultMap;
		}
		
		ThingCommand command=commandToThing.getCommand();
		
		thSet.stream().filter((th) -> !StringUtils.isEmpty(th.getFullKiiThingID())).forEach(thing -> {
			
			String version=thing.getSchemaVersion();
			int ver=1;
			try {
				ver = Integer.parseInt(version);
			}catch(Exception e){
				log.error("SchemaVersion invalid",e);
			}
			command.setSchemaVersion(ver);
			command.setSchema(thing.getSchemaName());
			
			try {
				String appID = thing.getKiiAppID();


				command.setUserID(appInfoManager.getDefaultOwer(appID));


				String cmdResult = thingIFService.sendCommand(command, thing.getFullKiiThingID());

				resultMap.put(thing.getId(),cmdResult);
			}catch(Exception ex){

				resultMap.put(thing.getId(),"error:"+ex.getMessage());
			}

		});

		return resultMap;
	}
	
	
	public Map<String,Object> doBusinessFunCall(BusinessFunctionParam function) {
		
		
		Map<String,Object> result=new HashMap<>();
		try {
			Object bean = context.getBean(function.getBeanName());
			
			
			
			
			Method method= MethodTools.getMethodByName(bean.getClass(),function.getFunctionName(),function.getParamList().size());
			
			Object returnResult=method.invoke(bean,function.getParamList());
			
			result.put("result",returnResult);
			
		} catch (IllegalAccessException|InvocationTargetException e) {
			
			result.put("exception",e.getMessage());
		}catch(NoSuchBeanDefinitionException e){
			result.put("exception",e.getMessage());
		}
		
		return result;
		
	}


}
