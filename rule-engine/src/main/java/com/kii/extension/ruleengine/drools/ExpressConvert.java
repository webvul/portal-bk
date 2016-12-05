package com.kii.extension.ruleengine.drools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

		StringBuffer sb=new StringBuffer();

		String result = express;

		Pattern pattern= Pattern.compile("\\$([ep])\\{([^\\}]+)\\}([i]?)");

		Matcher matcher=pattern.matcher(result);

		while(matcher.find()) {

			boolean isExp= "e".equals(matcher.group(1));

			String field = matcher.group(2);
			String isInt=matcher.group(3);

			String template = getExpTemplate(isCondition,isSimpleExp,isExp);

			String valueName="value";

			if(!StringUtils.isEmpty(isInt)) {
				valueName="numValue";
			}

//			if(!isCondition){
				valueName=StringUtils.capitalize(valueName);
//			}

			String replaceString= StrTemplate.gener(template,field,valueName);

			matcher.appendReplacement(sb,Matcher.quoteReplacement(replaceString));
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final String S_FIELD=" get${1}(\"${0}\") ";
	private static final String E_FIELD=" $ext.get${1}(\"${0}\") ";

	private static final String S_EXP=" $status.get${1}(\"${0}\") ";
	private static final String P_EXP=" $muiMap.get${1}(\"${0}\") ";
	private static final String E_EXP=" $ext.get${1}(\"${0}\") ";

	private String getExpTemplate(boolean isCondition,boolean isSimple,boolean isExt){

		if(isCondition) {
			if(isExt){
				return E_FIELD;
			}else{
				return S_FIELD;
			}
		}else{
			if (isExt) {
				return E_EXP;
			} else if (isSimple) {
				return S_EXP;
			} else {
				return P_EXP;
			}

		}
	}

}
