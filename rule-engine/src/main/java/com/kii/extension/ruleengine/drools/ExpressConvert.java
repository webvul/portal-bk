package com.kii.extension.ruleengine.drools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.common.utils.StrTemplate;

@Component
public class ExpressConvert {


	public String convertRightExpress(String express,boolean isSimpleExp){
		return convertExpress(express,false,isSimpleExp);

	}

	public  String convertExpress(String express) {

		return convertExpress(express,true,false);
	}




	private  String convertExpress(String express,boolean isCondition,boolean isSimpleExp){


		String  result=replaceFun(express);
		
		Param param=new Param(isCondition,isSimpleExp);
		return replaceParam(param, result);

	}


	private String replaceFun(String input){

		StringBuffer sb=new StringBuffer();

		Pattern pattern=Pattern.compile("([\\+\\-\\*\\/\\ \\!\\|\\&]+|^)([\\w\\.]+)\\(([^(]*)\\)");

		Matcher matcher=pattern.matcher(input);

		while(matcher.find()) {
			

			String prefix=matcher.group(1);
			
			String funName=matcher.group(2);

			String params=matcher.group(3);

			StringBuffer paramBuf=new StringBuffer();

			paramBuf.append(prefix).append("ExtFun.fun($triggerID,'").append(funName).append("'");

			if(StringUtils.isNotBlank(params)) {
				paramBuf.append(",");
				paramBuf.append(params);
			}
			paramBuf.append(")");
			matcher.appendReplacement(sb,Matcher.quoteReplacement(paramBuf.toString()));
		}

		matcher.appendTail(sb);

		return sb.toString();

	}
	
	private String replaceParam(Param param, String result) {
		
		StringBuffer sb=new StringBuffer();
		
		Pattern pattern= Pattern.compile("\\$([epth](\\:[iscm])?)\\{([^\\}]+)\\}");
		
		Matcher matcher=pattern.matcher(result);
		
		while(matcher.find()) {
			
			String field = matcher.group(3);
			String type=matcher.group(2);
			
			String replaceString = param.getExpress(matcher.group(1),type,field);
			
			matcher.appendReplacement(sb,Matcher.quoteReplacement(replaceString));
		}
		matcher.appendTail(sb);
		
		return sb.toString();
	}
	

	private static class Param{
		
		final boolean isCondition;
		
		final boolean  isSimpleExp;
		
		public Param(boolean isCondition,boolean isSimpleExp){
			this.isCondition=isCondition;
			this.isSimpleExp=isSimpleExp;
		}
		
		private static final String S_FIELD="get${1}(\"${0}\")";
		private static final String P_FIELD=S_FIELD;
		private static final String E_FIELD="$ext.get${1}(\"${0}\")";
		private static final String T_FIELD="$inst.get${1}(\"${0}\")";
		
		private static final String S_EXP="$status.get${1}(\"${0}\")";
		private static final String P_EXP="$muiMap.get${1}(\"${0}\")";
		private static final String E_EXP="$ext.get${1}(\"${0}\")";
		private static final String T_EXP="$inst.get${1}(\"${0}\")";
		
		private static final String[][] EXP_ARRAY={
				{E_FIELD,E_EXP},
				{T_FIELD,T_EXP},
				{S_FIELD,S_EXP},
				{P_FIELD,P_EXP}};
		
		private String getExpress(String sign,String type,String field){
			
			if(sign.length()>1){
				sign=sign.substring(0,1);
			}
			
			int idx2=isCondition?0:1;
			
			int idx=0;
			switch(sign){
				case "e":idx=0;break;
				case "t":idx=1;break;
				case "h":
				case "p":idx=isSimpleExp?2:3;
			}
			
			String template= EXP_ARRAY[idx][idx2];
			
			
			String valueName="value";
			if(type==null){
				type="s";
			}else{
				type=StringUtils.substring(type,1);
			}
			switch(type) {
				case "i":
					valueName= "numValue";
					break;
				case "c":
					valueName= "setValue";
					break;
				case "m":
					valueName= "mapValue";
					break;
				default:
			}
			
			if(sign.equals("h")){
				field="previous."+field;
			}
			
			valueName=StringUtils.capitalize(valueName);
			
			return  StrTemplate.gener(template,field,valueName);
			
			
		}
	}


}
