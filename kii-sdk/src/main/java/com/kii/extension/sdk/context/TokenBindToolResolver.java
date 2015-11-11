package com.kii.extension.sdk.context;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class TokenBindToolResolver {



	@Autowired
	private ApplicationContext context;


	private ThreadLocal<Boolean>  appChoiceLocal= ThreadLocal.withInitial(()->true);

	public void bindAdmin(){
		appChoiceLocal.set(true);
	}

	public void bindUser(){
		appChoiceLocal.set(false);
	}

	public String getToken(){


		if(appChoiceLocal.get()){

			return context.getBean(AdminTokenBindTool.class).getToken();
		}else{

			return context.getBean(UserTokenBindTool.class).getToken();
		}


	}

}
