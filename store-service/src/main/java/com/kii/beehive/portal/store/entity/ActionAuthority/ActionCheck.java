package com.kii.beehive.portal.store.entity.ActionAuthority;

public interface ActionCheck<T> {

	public boolean verify(T value);
}
