package com.kii.extension.sdk.context;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class TokenBindToolResolver {



	@Autowired
	private ApplicationContext context;


	private ThreadLocal<Boolean>  appChoiceLocal= ThreadLocal.withInitial(()->true);

	private ThreadLocal<String> tokenDirectLocal=new ThreadLocal<>();



	public void bindToken(String token){
		tokenDirectLocal.set(token);
	}

	public void bindAdmin(){
		appChoiceLocal.set(true);
	}

	public void bindUser(){
		appChoiceLocal.set(false);
	}

	public String getToken(){

		String token=tokenDirectLocal.get();
		if(!StringUtils.isEmpty(token)){
			return token;
		}

		if(appChoiceLocal.get()){

			return context.getBean(AdminTokenBindTool.class).getToken();
		}else{

			return context.getBean(UserTokenBindTool.class).getToken();
		}


	}

	public void reset(){
		tokenDirectLocal.remove();
	}


	public void clean(){

		appChoiceLocal.remove();

		tokenDirectLocal.remove();
	}


}
