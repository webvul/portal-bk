package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class AuthInfoEntry {
	
	private String userID;

	private String token;
	
	private Long teamID;
	
	private Set<String> permissionSet;
	
	private Map<String,Set<String>> headerMap=new HashMap<>();

	private Map<String,Set<String>> methodMap=new HashMap<>();

	public AuthInfoEntry(String userID,Long teamID, String token, Set<String> permissionSet){

		this.userID = userID;
		this.teamID = teamID;
		this.token = token;
		this.permissionSet = permissionSet;

		for(String permiss : permissionSet){

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

	/**
	 * get the header(function indicator, such as "/user", "/thing", etc) from url <br/>
	 * @param url
	 * @return
     */
	private String getHeader(String url) {
		int solIdx1=url.indexOf("/");
		int solIdx2=url.indexOf("/",solIdx1+1);
		solIdx2 = (solIdx2 == -1)? url.length() : solIdx2;

		return url.substring(solIdx1,solIdx2).trim();
	}

	/**
	 * validate whether user has permission to access to the method and subUrl
	 *
	 * @param subUrl it's supposed to start with the function indicator, such as "/user", "/thing", etc
	 * @param method
     * @return
     */
	public boolean doValid(String subUrl,String method){
//http://localhost:7070/mock/api/echo/simple

		Set<String> templateUrl=new HashSet<>();

		String header=getHeader(subUrl);

		if(!headerMap.containsKey(header) || !methodMap.containsKey(method.toUpperCase())) {
			return false;
		}

		templateUrl.addAll(headerMap.get(header));
		templateUrl.retainAll(methodMap.get(method.toUpperCase()));

		PathMatcher pathMatcher = new AntPathMatcher();
		return templateUrl.stream().filter(template-> pathMatcher.match(template, subUrl))
				.count()>0;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Set<String> getPermissionSet() {
		return permissionSet;
	}

	public void setPermissionSet(Set<String> permissionSet) {
		this.permissionSet = permissionSet;
	}
	
	public Long getTeamID() {
		return teamID;
	}

	public void setTeamID(Long teamID) {
		this.teamID = teamID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthInfoEntry [userID=");
		builder.append(userID);
		builder.append(", token=");
		builder.append(token);
		builder.append(", teamID=");
		builder.append(teamID);
		builder.append("]");
		return builder.toString();
	}
	
	
}
