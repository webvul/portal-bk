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


	private SafeThreadLocal<Boolean> appChoiceLocal= SafeThreadLocal.withInitial(()->true);

	public void bindAdmin(){

		appChoiceLocal.set(true);
	}

	public void bindUser(){

		appChoiceLocal.set(false);
	}

	String getToken(){


		if(appChoiceLocal.get()){

			return context.getBean(AdminTokenBindTool.class).getToken();
		}else{

			return context.getBean(UserTokenBindTool.class).getToken();
		}


	}



	public void clean(){

		appChoiceLocal.remove();
	}
	
	

}
