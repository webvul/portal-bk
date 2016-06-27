package test.mock;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.drools.CommandExec;


@Configuration
public class MockEventBus implements EventCallback {



	@Autowired
	private CommandExec exec;


	@Autowired
	protected ResourceLoader loader;



//	private String getDrlContent(String fileName) {
//
//		try {
//			return StreamUtils.copyToString(loader.getResource("classpath:com/kii/extension/ruleengine/"+fileName+".drl").getInputStream(), StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new IllegalArgumentException(e);
//		}
//
//	}


//	@Bean(name="testDroolsService")
//	public DroolsRuleService getCloudService(){
//
//		DroolsRuleService droolsService= new DroolsRuleService(false,
//				getDrlContent("triggerComm"),
//				getDrlContent("groupPolicy"),
//				getDrlContent("summaryCompute"));
//
//		droolsService.bindWithInstance("exec",exec);
//
//		return droolsService;
//
//	}


	@Override
	public void onTriggerFire(String triggerID, Map<String,String> params) {

	}
}
