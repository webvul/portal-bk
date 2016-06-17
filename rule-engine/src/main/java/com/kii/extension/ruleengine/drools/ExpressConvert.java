package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.common.utils.StrTemplate;

@Component
public class ExpressConvert {


	public String convertRightExpress(String express){
		return convertExpress(express,false);

	}

	public  String convertExpress(String express) {

		return convertExpress(express,true);
	}

	private  String convertExpress(String express,boolean sign){

		StringBuffer sb=new StringBuffer();

		String result = express;
		Pattern pattern= Pattern.compile("\\$([sep])\\{([^\\}]+)\\}([i]?)");

		Matcher matcher=pattern.matcher(result);

		while(matcher.find()) {

			String type = matcher.group(1);
			String field = matcher.group(2);
			String isInt=matcher.group(3);

			String template = patternMap.get(type);

			String valueName="value";

			if(!StringUtils.isEmpty(isInt)) {
				valueName="numValue";
			}

			if(!sign){
				template = expMap.get(type);
				valueName=StringUtils.capitalize(valueName);
			}

			String replaceString= StrTemplate.gener(template,field,valueName);

			matcher.appendReplacement(sb,Matcher.quoteReplacement(replaceString));
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final String S_FIELD=" ${1}(\"${0}\") ";
	private static final String P_FIELD=" ${1}(\"${0}\") ";
	private static final String E_FIELD=" $ext.${1}(\"${0}\") ";

	private static final String S_EXP=" $status.get${1}(\"${0}\") ";
	private static final String P_EXP=" $muiMap.get${1}(\"${0}\") ";
	private static final String E_EXP=" $ext.get${1}(\"${0}\") ";

	private static final Map<String,String> patternMap=new HashMap<>();
	private static final Map<String,String> expMap=new HashMap<>();

	static{

		patternMap.put("s",S_FIELD);
		patternMap.put("p",P_FIELD);
		patternMap.put("e",E_FIELD);

		expMap.put("s",S_EXP);
		expMap.put("p",P_EXP);
		expMap.put("e",E_EXP);

	}




}
