package com.kii.beehive.portal.entitys;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.kii.beehive.portal.auth.UrlTemplateVerify;

public class PermissionTree {



	private String displayName="EMPTY";

	private String url="-";

	private String fullPath="-";

	private String method="*";

	private Map<String,PermissionTree> submodule=new HashMap<>();





	public PermissionTree clone(){

		PermissionTree entry=new PermissionTree();

		entry.setDisplayName(displayName);
		entry.setUrl(url);
		entry.setFullPath(fullPath);
		entry.setMethod(method);

		submodule.forEach((k,v)->entry.submodule.put(k,v.clone()));

		return entry;
	}

	private  void fillIndexMap(String upperPath,Map<String,String> map) {

		this.fullPath = upperPath;

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

		if(pattern.getNextLevel().isEmpty()){
			return;
		}
		submodule.keySet().retainAll(pattern.getNextLevel());

		submodule.forEach((k,v)->v.doAcceptFilter(pattern.getNextPattern(k)));
	}

	public boolean doDenyFilter(PatternSet pattern) {

		Set<String> removedSet=submodule.keySet().stream().filter(k->{

			PatternSet nextPattern=pattern.getNextPattern(k);

			if(nextPattern==null){
				return false;
			}

			if(nextPattern.getNextLevel().isEmpty()){
				return true;
			}

			return submodule.get(k).doDenyFilter(nextPattern);

		}).collect(Collectors.toSet());

		submodule.keySet().removeAll(removedSet);

		return  submodule.keySet().isEmpty();
	}

	private boolean removed(PatternSet pattern){
		return pattern.getNextLevel().isEmpty();
	}


	public boolean doVerify(String method,String url){

		if(submodule.isEmpty()){
			return true;
		}
		Optional<PermissionTree>  optional=submodule.values().stream().filter((t)-> t.verify(method,url)).findFirst();

		if(!optional.isPresent()){
			return false;
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
