package com.kii.beehive.portal.entitys;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.kii.beehive.portal.auth.UrlTemplateVerify;
import com.kii.beehive.portal.common.utils.SubStrUtils;

public class PermissionTree {



	private String displayName="EMPTY";

	private String url="-";

	private String fullPath="-";

	private String method="*";

	private String name;

	private boolean fullMatch=false;

	private boolean isDeny=false;

	private Map<String,PermissionTree> submodule=new HashMap<>();


	public PermissionTree clone(){

		PermissionTree entry=new PermissionTree();

		entry.setDisplayName(displayName);
		entry.setUrl(url);
		entry.setFullPath(fullPath);
		entry.setMethod(method);

		entry.fullMatch=this.fullMatch;
		entry.isDeny=this.isDeny;
		entry.name=this.name;

		submodule.forEach((k,v)->entry.submodule.put(k,v.clone()));

		return entry;
	}

	private  void fillIndexMap(String upperPath,Map<String,String> map) {

		this.fullPath = upperPath;

		this.name= SubStrUtils.getAfterSep(fullPath,'.');

		submodule.forEach((k, v) -> {
			String fullPath = upperPath + "." + k;
			map.put(k, fullPath);
			v.fillIndexMap(fullPath, map);
		});
	}



	public Map<String,String>  fillFullPath(){

		Map<String,String> map=new HashMap<>();

		map.put("root","root");

		fillIndexMap("root",map);

		return map;

	}

	public void doAcceptFilter(PatternSet pattern) {

		doFilter(pattern,false);
//		isDeny=false;
//
//		if(pattern.isBranchStop()){
//			this.fullMatch=true;
//			return;
//		}
//		this.fullMatch=false;
//		submodule.keySet().retainAll(pattern.getNextLevel());
//
//		submodule.forEach((k,v)->v.doFilter(pattern.getNextPattern(k),false));
	}


	private  void doFilter(PatternSet pattern,boolean isDeny) {

		this.isDeny=isDeny;

		if(pattern.isBranchStop()){
			this.fullMatch=true;
			return;
		}
		this.fullMatch=false;
		submodule.keySet().retainAll(pattern.getNextLevel());

		submodule.forEach((k,v)->v.doFilter(pattern.getNextPattern(k),isDeny));
	}

	public boolean doDenyFilter(PatternSet pattern) {

		doFilter(pattern,true);

		return false;

//		Set<String> removedSet=new HashSet<>();
//
//		for(Map.Entry<String,PermissionTree>  entry:submodule.entrySet()){
//
//			String key=entry.getKey();
//			PermissionTree subTree=entry.getValue();
//
//			PatternSet nextPattern=pattern.getNextPattern(key);
//			if(nextPattern==null){
//				continue;
//			}
//
//			if(nextPattern.isBranchStop()){
//				removedSet.add(key);
//				continue;
//			}
//
//			if(subTree.doDenyFilter(nextPattern)){
//				removedSet.add(key);
//			};
//		}
//
//		submodule.keySet().removeAll(removedSet);
//
//		return  submodule.keySet().isEmpty();
	}

	public boolean doVerify(String url){

		return doVerify("*",url);
	}

	public boolean doVerify(String method,String url){


		if(!verify(method,url)){
			return false^isDeny;
		};

		if(fullMatch){
			return true^isDeny;
		}

		Optional<PermissionTree>  optional=submodule.values().stream().filter((t)-> t.verify(method,url)).findFirst();

		if(!optional.isPresent()){
			return false^isDeny;
		}

		return optional.get().doVerify(method,url);

	}

	private boolean verify(String method,String url){

		if(!this.method.equals("*")  &&  !this.method.toLowerCase().equals(method.toLowerCase())){
			return false;
		}


		return UrlTemplateVerify.verfiyUrlTemplate(this.url,url);
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}


	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, PermissionTree> getSubmodule() {
		return submodule;
	}

	public void setSubmodule(Map<String, PermissionTree> submodule) {
		this.submodule = submodule;
	}
	

}
