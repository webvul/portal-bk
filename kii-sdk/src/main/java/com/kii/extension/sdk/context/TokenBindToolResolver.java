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


	private ThreadLocal<Boolean>  appChoiceLocal= ThreadLocal.withInitial(()->true);

	public void bindAdmin(){

		log.debug("bindAdmin");
		appChoiceLocal.set(true);
	}

	public void bindUser(){

		log.debug("bindUser");
		appChoiceLocal.set(false);
	}

	String getToken(){


		if(appChoiceLocal.get()){

			log.debug("use AdminTokenBindTool to get token");
			return context.getBean(AdminTokenBindTool.class).getToken();
		}else{

			log.debug("use UserTokenBindTool to get token");
			return context.getBean(UserTokenBindTool.class).getToken();
		}


	}



	public void clean(){

		log.debug("clean");
		appChoiceLocal.remove();
	}
	
	

}
