package com.kii.beehive.portal.common.utils;

import java.lang.reflect.Method;

public class MethodTools {
	
	
	public static Method getMethodByName(Class cls, String methodName,int paramNumber){
		
		Method choice=null;
		for(Method m:cls.getMethods()){
			
			if(m.getName().equals(methodName)&&m.getParameterCount()==paramNumber){
				choice= m;
			}
		}
		
		if(choice==null) {
			for (Method m : cls.getDeclaredMethods()) {
				
				if (m.getName().equals(methodName) && m.getParameterCount() == paramNumber) {
					choice = m;
				}
			}
		}
		
		choice.setAccessible(true);
		
		return choice;
	}
}
