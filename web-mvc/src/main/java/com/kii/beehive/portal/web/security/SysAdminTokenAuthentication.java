package com.kii.beehive.portal.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Created by hdchen on 7/13/16.
 */
public class SysAdminTokenAuthentication implements Authentication {
	private List<GrantedAuthority> authorityList = new ArrayList();

	private String authToken;

	private boolean authenticated;

	public SysAdminTokenAuthentication(String token) {
		authToken = token;
		for (Role role : Role.values()) {
			authorityList.add(new SimpleGrantedAuthority(role.name()));
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorityList;
	}

	@Override
	public Object getCredentials() {
		return authToken;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return getName();
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		authenticated = isAuthenticated;
	}

	@Override
	public String getName() {
		return Role.administrator.name();
	}
}
