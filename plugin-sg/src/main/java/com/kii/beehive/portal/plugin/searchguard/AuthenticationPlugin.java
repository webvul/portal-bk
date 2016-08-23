package com.kii.beehive.portal.plugin.searchguard;

import org.elasticsearch.action.ActionModule;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestModule;
import com.kii.beehive.portal.plugin.searchguard.rest.SearchAction;

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

	public void onModule(final RestModule module) {
		module.addRestAction(SearchAction.class);
	}

	public void onModule(final ActionModule module) {

	}
}
