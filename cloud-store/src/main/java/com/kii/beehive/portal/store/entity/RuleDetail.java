package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.Set;

public class RuleDetail extends  PortalEntity{


	private String roleName;

	private Set<String> patternSet= new HashSet<>();

	@Override
	public String getId(){
		return roleName;
	}

	@Override
	public void setId(String id){
		this.roleName=id;
	}

	public Set<String> getPatternSet() {
		return patternSet;
	}

	public void setPatternSet(Set<String> patternSet) {
		this.patternSet = patternSet;
	}
}
