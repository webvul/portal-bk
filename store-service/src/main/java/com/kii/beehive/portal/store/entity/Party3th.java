package com.kii.beehive.portal.store.entity;

import java.util.Map;
import java.util.Set;

import com.kii.beehive.portal.store.entity.ActionAuthority.ActionCheck;

public class Party3th {

	private String id;

	private Map<String,ActionCheck> actionMap;

	private Map<String,Set<String>> statusMap;

	private Set<String> things;
}
