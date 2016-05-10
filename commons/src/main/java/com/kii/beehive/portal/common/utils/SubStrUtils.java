package com.kii.beehive.portal.common.utils;

public class SubStrUtils {

	public final static String getBeforeSep(String str,char sep){
		int idx=str.indexOf(sep);

		if(idx==-1){
			return str;
		}

		return str.substring(0,idx).trim();
	}

	public final static String getAfterSep(String str,char sep){
		int idx=str.indexOf(sep);

		if(idx==-1){
			return str;
		}
		return str.substring(idx+1,str.length()).trim();
	}
}
