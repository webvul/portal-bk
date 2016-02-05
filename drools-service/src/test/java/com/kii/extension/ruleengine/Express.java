package com.kii.extension.ruleengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Express {

	private static Logger log= LoggerFactory.getLogger(Express.class);

	public static boolean doCompute(int status,String  express){

		log.info("the express: "+express+" status:"+status);

//		return ;
		return Integer.parseInt(express)>status;
	}
}
