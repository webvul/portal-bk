package com.kii.beehive.portal.plugin.searchguard;

import org.elasticsearch.plugins.Plugin;

/**
 * Created by hdchen on 6/28/16.
 */
public class AuthenticationPlugin extends Plugin {
	@Override
	public String name() {
		return "BeehiveAuthentication";
	}

	@Override
	public String description() {
		return "An authentication backend for SearchGuard2";
	}
}
