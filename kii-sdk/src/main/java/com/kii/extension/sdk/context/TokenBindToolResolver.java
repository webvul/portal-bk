package com.kii.extension.sdk.context;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.common.utils.SafeThreadLocal;


@Component
public class TokenBindToolResolver {

	private Logger log= LoggerFactory.getLogger(TokenBindToolResolver.class);


	@Autowired
	private ApplicationContext context;

	private SafeThreadLocal<String> appChoiceLocal= SafeThreadLocal.withInitial(()-> TokenBindTool.BindType.None.name());
	
	public void bindByType(String name) {
		appChoiceLocal.set(name);
		
	}


	public void bindUser(String token){

		appChoiceLocal.set(TokenBindTool.BindType.user.name());
		context.getBean(UserTokenBindTool.class).bindToken(token);
	}

	public void bindThing(String token){

		appChoiceLocal.set(TokenBindTool.BindType.thing.name());
		context.getBean(ThingTokenBindTool.class).bindToken(token);

	}

	String getToken(){
		
		TokenBindTool tool = getTokenBindTool();
		
		return tool.getToken();

	}
	
	private TokenBindTool getTokenBindTool() {
		Map<String,TokenBindTool> tools=context.getBeansOfType(TokenBindTool.class);
		
		return tools.values().stream().filter((t)->t.getBindName().equals(appChoiceLocal.get())).findFirst().get();
	}
	
	
	public void clean(){

		appChoiceLocal.remove();
	}
	
	
	public void refreshToken() {
		TokenBindTool tool = getTokenBindTool();
		
		tool.refreshToken();
		
	}
}
