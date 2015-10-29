package com.kii.beehive.portal.store.entity.ActionAuthority;

import java.util.Set;

public class EnumCheck implements ActionCheck<String> {

	private Set<String> validSet;
	@Override
	public boolean verify(String value) {
		value=value.trim().toLowerCase();
		return validSet.contains(value);
	}
}
