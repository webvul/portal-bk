package com.kii.beehive.portal.store.entity.trigger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MemberState {

	private Map<String,Boolean> memberStatusMap=new HashMap<>();

	@JsonAnyGetter
	public Map<String, Boolean> getMemberStatusMap() {
		return memberStatusMap;
	}

	@JsonAnySetter
	public void setMemberStatus(String key,Boolean value) {
		this.memberStatusMap.put(key,value);
	}

	@JsonIgnore
	public Set<String> getDeletedIDs(Collection<String> newIDs){

		Set<String> ids=new HashSet<>(memberStatusMap.keySet());

		ids.removeAll(newIDs);

		return ids;
	}

	@JsonIgnore
	public Set<String> getAddedIDs(Collection<String> newIDs){

		Set<String> ids=memberStatusMap.keySet();

		Set<String> newIDSet=new HashSet<>(newIDs);

		newIDSet.removeAll(ids);

		return newIDSet;
	}

	@JsonIgnore
	public boolean checkPolicy(TriggerGroupPolicy.TriggerGroupPolicyType policy,int criticalNumber){


		long accessNum=memberStatusMap.values().stream().filter(v->v).count();

		long sumNum=memberStatusMap.size();

		switch(policy){
			case All:
				return sumNum==accessNum;
			case Any:
				return accessNum>0;
			case Some:
				return accessNum>criticalNumber;
			case Percent:
				return 100*accessNum>(criticalNumber*sumNum);
			default:
				return false;
		}

	}
}
