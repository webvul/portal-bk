package com.kii.beehive.portal.common.utils;

public class CommLangsUtils {
	
	public static boolean safeEquals(Object a,Object b){
		
		if(a==null&&b==null){
			return true;
		}
		
		if(a==null||b==null){
			return false;
		}
		
		return a.equals(b);
	}
}
