package com.kii.extension.sdk.context;


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

	private SafeThreadLocal<Integer> appChoiceLocal= SafeThreadLocal.withInitial(()->0);

	public void bindAdmin(){

		appChoiceLocal.set(0);
	}

	public void bindUser(){

		appChoiceLocal.set(1);
	}

	public void bindThing(){

		appChoiceLocal.set(2);

	}



	public void bindUser(String token){

		appChoiceLocal.set(1);
		context.getBean(UserTokenBindTool.class).bindToken(token);
	}

	public void bindThing(String token){

		appChoiceLocal.set(2);
		context.getBean(ThingTokenBindTool.class).bindToken(token);

	}

	String getToken(){


		switch(appChoiceLocal.get()){

			case 1:return context.getBean(UserTokenBindTool.class).getToken();
			case 2:return context.getBean(ThingTokenBindTool.class).getToken();
			default:return context.getBean(AdminTokenBindTool.class).getToken();

		}

	}



	public void clean(){

		appChoiceLocal.remove();
	}
	
	

}
