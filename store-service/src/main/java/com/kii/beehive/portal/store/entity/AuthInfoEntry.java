package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.kii.beehive.portal.jdbc.entity.AuthInfo;

public class AuthInfoEntry {


	public AuthInfoEntry(AuthInfo info){
		this.userID=info.getUserID();

		for(String permiss:info.getPermisssionSet()){

			int spaceIdx=permiss.indexOf(" ");

			String method=permiss.substring(0,spaceIdx).trim().toUpperCase();

			String url=permiss.substring(spaceIdx).trim();

			Set<String>  set=methodMap.getOrDefault(method,new HashSet<String>());
			set.add(url);
			methodMap.put(method,set);

			String header = getHeader(url);
			Set<String> headers=headerMap.getOrDefault(header,new HashSet<String>());
			headers.add(url.trim());
			headerMap.put(header,headers);

		}

	}

	private String getHeader(String url) {
		int solIdx1=url.indexOf("/");
		int solIdx2=url.indexOf("/",solIdx1+1);

		return url.substring(solIdx1,solIdx2).trim();
	}

	private Map<String,Set<String>> headerMap=new HashMap<>();

	private Map<String,Set<String>> methodMap=new HashMap<>();


	public boolean doValid(String subUrl,String method){
//http://localhost:7070/mock/api/echo/simple

		Set<String> templateUrl=new HashSet<>();

		String header=getHeader(subUrl);

		templateUrl.addAll(headerMap.get(header));
		templateUrl.retainAll(methodMap.get(method.toUpperCase()));

		PathMatcher pathMatcher = new AntPathMatcher();
		return templateUrl.stream().filter(template-> pathMatcher.match(template, subUrl))
				.count()>0;
	}

	private String userID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
}
