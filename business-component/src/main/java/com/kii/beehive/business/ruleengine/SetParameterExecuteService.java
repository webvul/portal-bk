package com.kii.beehive.business.ruleengine;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.service.BusinessObjDao;
import com.kii.extension.ruleengine.service.ExecuteResultDao;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.task.ExceptionInfo;
import com.kii.extension.ruleengine.store.trigger.task.SettingParameterResponse;
import com.kii.extension.ruleengine.store.trigger.task.SettingTriggerGroupParameter;

@Component
public class SetParameterExecuteService {
	
	
	@Autowired
	private ExecuteResultDao resultDao;

	
	@Autowired
	private BusinessObjDao businessObjDao;
	
	
	
	@Autowired
	private ResponseBuilder  builder;
	
	@Lazy
	@Autowired
	private TriggerOperate  operate;
	
	
	
	public void settingParam(SettingTriggerGroupParameter settingParams, ExecuteParam params) {
		
		SettingParameterResponse result=builder.getSettingParamResponse(params);
		
		try {
			
			
			BusinessDataObject obj=settingParams.getBusinessObj();
			
			settingParams.getParamMap().forEach((k,v)->{
				
				Object value=params.getBusinessParams().get(k);
				
				obj.getData().put(k,value);
			});
			
			operate.addBusinessData(obj);
			
			businessObjDao.addBusinessObj(obj);
			
		} catch(Exception e){
			result.setExceptionInfo(new ExceptionInfo(e));
		}
		
	}

}
