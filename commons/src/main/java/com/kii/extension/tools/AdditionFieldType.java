package com.kii.extension.tools;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum AdditionFieldType {
	
	
	Str,Int;
	
	public String getKiiAppFieldName(int idx){
		return "additions."+name()+"idx";
	}
	
	static Pattern pattern=Pattern.compile("(Str|Int)(\\d+)");
	
	static Pattern strPattern=Pattern.compile("Str(\\d+)");
	
	static Pattern intPattern=Pattern.compile("Int(\\d+)");
	
	public static AdditionFieldType getType(String fieldName){
		if(strPattern.matcher(fieldName).find()){
			return Str;
		}else if(intPattern.matcher(fieldName).find()){
			return Int;
		}else{
			return null;
		}
	}
	
	public static int getIndex(String fieldName){
		Matcher matcher=pattern.matcher(fieldName);
		if(matcher.find()) {
			
			return Integer.parseInt(matcher.group(2));
		}else{
			return -1;
		}
	}
	
	public static boolean verifyFieldName(String fieldName){
		return pattern.matcher(fieldName).find();
	}
}
