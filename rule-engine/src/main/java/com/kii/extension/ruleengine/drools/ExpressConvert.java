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
	
	Pattern  beginPat=Pattern.compile("\\$([ept](\\_[isc])?)(\\{)");
	
	Pattern endPat=Pattern.compile("(\\})");
	
	
	private String replaceParam(Param param,String input){
		
		Matcher matcher=beginPat.matcher(input);
		if(!matcher.find()){
			return input;
		}
		
		String newInput=find(param,matcher,input);
		return replaceParam(param,newInput);
		
	}
	
	private String find(Param param, Matcher matcher,String input) {
		
	
		
		String header=input.substring(0,matcher.start());
		String rest=input.substring(matcher.end());
		
		Matcher nextStart=beginPat.matcher(rest);
		
		Matcher matcherEnd=endPat.matcher(rest);
		
		if (!matcherEnd.find()) {
			throw new IllegalArgumentException("cannot match {}");
		}
		
		if (!nextStart.find()){
			String full =header+ findEnd(param, rest,matcher.group(1),matcherEnd);
			return full;
		}else if(matcherEnd.start()  > nextStart.start()) {
			
			String tail=replaceParam(param,rest);
			
			String prefix=input.substring(0,matcher.end());
			return replaceParam(param,prefix+tail);
		} else {
			String embedded = header+findEnd(param, rest,matcher.group(1),matcherEnd);
			return replaceParam(param, embedded);
		}
		
	}

	
	
	private String findEnd(Param param,String input,String prefix,Matcher matchEnd){
		
		int end=matchEnd.end(1);
		
		String exp=input.substring(0,end-1);
		
		String template = param.getExpTemplate(prefix);
		
		String valueName="Value";
		
		if(prefix.length()>1) {
			
			String sign=prefix.substring(prefix.length()-1);
			if ("i".equals(sign)) {
				valueName = "NumValue";
			} else if ("s".equals(sign)) {
				valueName = "SetValue";
			}
		}
		String replaceString= StrTemplate.gener(template,exp,valueName);
		
		return replaceString+input.substring(end);
		
	}

	private static class Param{
		
		final boolean isCondition;
		
		final boolean  isSimpleExp;
		
		public Param(boolean isCondition,boolean isSimpleExp){
			this.isCondition=isCondition;
			this.isSimpleExp=isSimpleExp;
		}
		
		private static final String S_FIELD=" get${1}(\'${0}\')";
		private static final String P_FIELD=S_FIELD;
		private static final String E_FIELD=" $ext.get${1}(\'${0}\')";
		private static final String T_FIELD=" $inst.get${1}(\'${0}\')";
		
		private static final String S_EXP=" $status.get${1}(\'${0}\')";
		private static final String P_EXP=" $muiMap.get${1}(\'${0}\')";
		private static final String E_EXP=" $ext.get${1}(\'${0}\')";
		private static final String T_EXP=" $inst.get${1}(\'${0}\')";
		
		private static final String[][] EXP_ARRAY={
				{E_FIELD,E_EXP},
				{T_FIELD,T_EXP},
				{S_FIELD,S_EXP},
				{P_FIELD,P_EXP}};
		
		private String getExpTemplate(String sign){
			
			if(sign.length()>1){
				sign=sign.substring(0,1);
			}
			
			int idx2=isCondition?0:1;
			
			int idx=0;
			switch(sign){
				case "e":idx=0;break;
				case "t":idx=1;break;
				case "p":idx=isSimpleExp?2:3;
			}
			
			return EXP_ARRAY[idx][idx2];
		}
	}


}
