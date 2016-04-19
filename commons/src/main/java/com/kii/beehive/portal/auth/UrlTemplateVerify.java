package com.kii.beehive.portal.auth;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class UrlTemplateVerify {


	private static PathMatcher pathMatcher = new AntPathMatcher();

	public static boolean verfiyUrlTemplate(String template,String target){

		return pathMatcher.match(template,target);

	}
}
