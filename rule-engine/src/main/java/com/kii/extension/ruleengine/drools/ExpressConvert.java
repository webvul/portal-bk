package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kii.beehive.portal.common.utils.StrTemplate;

public class ExpressConvert {



	public  String convertExpress(String express){

		StringBuffer sb=new StringBuffer();

		String result = express;
		Pattern pattern= Pattern.compile("\\$([sep])\\{([^\\}]+)\\}");

		Matcher matcher=pattern.matcher(result);

		while(matcher.find()) {

			String type = matcher.group(1);
			String name = matcher.group(2);
			String replaceString = doReplace(type, name);

			matcher.appendReplacement(sb,replaceString);
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final String S_FIELD=" values[\"${0}\"] ";

	private static final String P_FIELD=" values[\"${0}\"] ";

	private static final String E_FIELD=" ext.params[\"${0}\"] ";


	private static final Map<String,String> patternMap=new HashMap<>();

	static{

		patternMap.put("s",S_FIELD);
		patternMap.put("p",P_FIELD);
		patternMap.put("e",E_FIELD);
	}

	private String doReplace(String type,String name){

		String pattern=patternMap.get(type);

		return StrTemplate.gener(pattern,name);

	}


}
