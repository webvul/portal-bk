package com.kii.extension.sdk.context;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class TokenBindToolResolver {

	private Logger log= LoggerFactory.getLogger(TokenBindToolResolver.class);


	@Autowired
	private ApplicationContext context;



	private ThreadLocal<Integer>  appChoiceLocal= ThreadLocal.withInitial(()->0);

	public void bindAdmin(){

		appChoiceLocal.set(0);
	}

	public void bindUser(){

		appChoiceLocal.set(1);
	}

	public void bindThing(){

		appChoiceLocal.set(2);

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
