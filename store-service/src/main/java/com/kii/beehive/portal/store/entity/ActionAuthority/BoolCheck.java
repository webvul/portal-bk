package com.kii.beehive.portal.store.entity.ActionAuthority;

public class BoolCheck implements ActionCheck<Boolean>{

	private boolean validVal;

	@Override
	public boolean verify(Boolean value) {
		return validVal==value;
	}
}
