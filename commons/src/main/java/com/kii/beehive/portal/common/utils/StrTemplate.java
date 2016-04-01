package com.kii.beehive.portal.common.utils;

import java.util.Map;
import java.util.regex.Matcher;

public class StrTemplate {

	public static final String generUrl(String template,
										Map<String, String> params) {
		String result = template;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();

			String token = "\\$\\(" + key + "\\)";

			String  value= Matcher.quoteReplacement(entry.getValue());

			result = result.replaceAll(token, value);
		}

		return result;
	}

	public static final String generUrl(String template, String... params) {

		if(params==null ||  params.length==0){
			return template;
		}
		String result = template;
		for (int i = 0; i < params.length; i++) {
			String token = "\\$\\(" + i + "\\)";

			result = result.replaceAll(token, params[i]);
		}

		return result;
	}

	public static final String generByMap(String template, Map<String, String> params) {
		String result = template;

		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();

			String token = "\\$\\{" + key + "\\}";

			String  value= Matcher.quoteReplacement(entry.getValue());
			//TODO:do not using regExp

			result = result.replaceAll(token, value);
		}

		return result;
	}

	public static final String gener(String template, String... params) {

		if(params==null ||  params.length==0){
			return template;
		}
		String result = template;
		for (int i = 0; i < params.length; i++) {
			String token = "\\$\\{" + i + "\\}";

			String  value= Matcher.quoteReplacement(params[i]);

			result = result.replaceAll(token,value);
		}

		return result;
	}

//	public static String generByObj(String template, Object entity) {
//		Pattern exp=Pattern.compile("\\$\\{([^}]+)\\}");
//
//		StringBuffer buf=new StringBuffer();
//		Matcher match=exp.matcher(template);
//
//		int start=0;
//		while(match.find()){
//			String field=match.group(1);
//
//			int begin=match.start();
//			buf.append(template.subSequence(start,begin));
//			try {
//				buf.append(BeanUtils.getProperty(entity, field));
//			} catch (Exception e) {
//				e.printStackTrace();
//				buf.append(field);
//			}
//			start=match.end();
//		}
//
//		buf.append(template.substring(start));
//
//		return buf.toString();
//	}

}
