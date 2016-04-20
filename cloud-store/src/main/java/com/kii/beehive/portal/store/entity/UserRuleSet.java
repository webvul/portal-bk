package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.Set;

import com.kii.extension.sdk.entity.KiiEntity;

public class UserRuleSet  extends KiiEntity {


	private String ruleName;

	private Set<String> acceptRuleSet=new HashSet<>();

	private Set<String> denyRuleSet=new HashSet<>();


	@Override
	public String getId(){
		return ruleName;
	}

	@Override
	public void setId(String id){

	}

	public Set<String> getDenyRuleSet() {
		return denyRuleSet;
	}

	public void setDenyRuleSet(Set<String> denyRuleSet) {
		this.denyRuleSet = denyRuleSet;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Set<String> getAcceptRuleSet() {
		return acceptRuleSet;
	}

	public void setAcceptRuleSet(Set<String> acceptRuleSet) {
		this.acceptRuleSet = acceptRuleSet;
	}



}
