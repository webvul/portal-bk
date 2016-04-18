package com.kii.beehive.portal.entitys;

import java.util.HashMap;
import java.util.Map;

public class PermissionEntry {

	private String displayName;

	private String url;

	private Map<String,PermissionEntry> submodule=new HashMap<>();


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

	public Map<String, PermissionEntry> getSubmodule() {
		return submodule;
	}

	public void setSubmodule(Map<String, PermissionEntry> submodule) {
		this.submodule = submodule;
	}
}
